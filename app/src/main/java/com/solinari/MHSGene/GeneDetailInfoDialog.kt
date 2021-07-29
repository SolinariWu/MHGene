package com.solinari.MHSGene

import android.app.Application
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.solinari.MHSGene.databinding.DialogGeneDetailInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

private const val GENE_DETAIL_INFO_URL = "https://egg.linebots.com.tw/ajax/skill_info.json"
const val GENE_DETAIL_INFO_DIALOG = "GeneDetailInfoDialog"

class GeneDetailInfoDialog : DialogFragment() {

    private lateinit var gene: Gene
    private lateinit var geneDetailInfo: GeneDetailInfo
    private var jobList = ArrayList<Job>()
    private lateinit var binding: DialogGeneDetailInfoBinding

    companion object {
        fun newInstance(gene: Gene): GeneDetailInfoDialog {
            return GeneDetailInfoDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("gene", gene)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGeneDetailInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog_MinWidth)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gene = requireArguments().getParcelable<Gene>("gene") ?: Gene()
        if (gene.id < 1) {
            error()
            return
        }

        binding.geneIcon.setImageResource(gene.getGeneIcon())
        binding.geneName.text = gene.name
        binding.close.setOnClickListener { dismiss() }
        binding.update.setOnClickListener {
            binding.progressBar.show()
            fetchGeneDetailInfo()
        }

        checkData()
        fetchGeneDetailInfo()
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (jobList.size > 0) {
            jobList.forEach {
                it.cancel()
            }
        }
        super.onDismiss(dialog)
    }


    private fun checkData() {
        activity?.let {
            val dao = GeneDataBase.getDatabase(it).GeneDao()

            val job = CoroutineScope(Dispatchers.Main).launch {
                if (dao.getAllGeneDetailInfo().isNotEmpty()) {
                    geneDetailInfo = dao.getGeneDetailInfo(gene.id)
                    setInfo()
                }
                else {
                    fetchGeneDetailInfo()
                }
            }
            jobList.add(job)
        }
    }

    private fun fetchGeneDetailInfo() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(GENE_DETAIL_INFO_URL)
            .get()


        client.newCall(request.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                error()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.body == null) {
                    error()
                    return
                }

                activity?.let {
                    val respBody = response.body!!.string()
                    val sp = it.getSharedPreferences(MHGENE_SP, Application.MODE_PRIVATE)
                    val dao = GeneDataBase.getDatabase(it).GeneDao()

                    val job = CoroutineScope(Dispatchers.Main).launch {
                        val jsonElement = JsonParser().parse(respBody)
                        if (jsonElement is JsonObject && jsonElement.has("version") && jsonElement.has("count")) {
                            //如果版本相同用本地資料即可
                            if (jsonElement.get("version").asString != sp.getString(GENE_DETAIL_INFO_VERSION_SP, "")) {
                                sp.edit().putString(GENE_DETAIL_INFO_VERSION_SP, jsonElement.get("version").asString).apply()
                                val list = ArrayList<GeneDetailInfo>()
                                val count = jsonElement.get("count").asInt
                                for (i in 1..count) {
                                    val geneDetailInfoObject = jsonElement.get(i.toString()).asJsonObject
                                    val item = GeneDetailInfo().apply {
                                        id = geneDetailInfoObject.get("id").asInt
                                        name = geneDetailInfoObject.get("name").asString
                                        skill = geneDetailInfoObject.get("skill").asString
                                        type = geneDetailInfoObject.get("type").asString
                                        cost = geneDetailInfoObject.get("cost").asString
                                        desc = geneDetailInfoObject.get("desc").asString
                                        from = geneDetailInfoObject.get("from").asString
                                    }
                                    list.add(item)
                                    if (item.id == gene.id) {
                                        geneDetailInfo = item
                                    }
                                }
                                setInfo()
                                dao.insertGeneDetailInfo(list)
                            }
                            else {
                                geneDetailInfo = dao.getGeneDetailInfo(gene.id)
                                setInfo()
                            }
                        }
                        else {
                            error()
                        }
                    }
                    jobList.add(job)
                }
            }
        })
    }

    private fun setInfo() {
        activity?.let {
            if (this::geneDetailInfo.isInitialized && this::binding.isInitialized) {
                binding.progressBar.hide()
                binding.skill.text = geneDetailInfo.skill
                binding.cost.text = it.getString(R.string.gene_detail_info_cost, geneDetailInfo.cost)
                binding.desc.text = it.getString(R.string.gene_detail_info_desc, geneDetailInfo.desc)
                binding.from.text = it.getString(R.string.gene_detail_info_from, geneDetailInfo.from)
            }
        }
    }

    private fun error() {
        CoroutineScope(Dispatchers.Main).launch {
            activity?.let { Toast.makeText(it, R.string.gene_detail_info_error, Toast.LENGTH_LONG).show() }
            dismiss()
        }
    }
}