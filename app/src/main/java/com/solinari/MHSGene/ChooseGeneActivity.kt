package com.solinari.MHSGene

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.solinari.MHSGene.databinding.ActivityChooseGeneBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ChooseGeneActivity : AppCompatActivity(), GeneAdapter.GeneClickListener {

    private lateinit var binding: ActivityChooseGeneBinding
    private lateinit var adapter: GeneAdapter
    private lateinit var rainbowGene: Gene
    private val attrList = ArrayList<String>()
    private val typeList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseGeneBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initList()
    }

    private fun initList() {
        adapter = GeneAdapter(this)
        binding.geneList.adapter = adapter
        binding.geneList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.listener = this

        CoroutineScope(Dispatchers.Main).launch {
            val dao = GeneDataBase.getDatabase(this@ChooseGeneActivity).GeneDao()
            rainbowGene = dao.getRainbowGene()
            if (!this@ChooseGeneActivity.isDestroyed && this@ChooseGeneActivity::adapter.isInitialized && this@ChooseGeneActivity::rainbowGene.isInitialized){
                adapter.addGeneToFirst(rainbowGene)
            }
        }
        setClick()
    }

    private fun setClick() {
        binding.non.setOnCheckedChangeListener(typeClickListener)
        binding.fire.setOnCheckedChangeListener(typeClickListener)
        binding.water.setOnCheckedChangeListener(typeClickListener)
        binding.thunder.setOnCheckedChangeListener(typeClickListener)
        binding.ice.setOnCheckedChangeListener(typeClickListener)
        binding.dragon.setOnCheckedChangeListener(typeClickListener)
        binding.noType.setOnCheckedChangeListener(attrClickListener)
        binding.speed.setOnCheckedChangeListener(attrClickListener)
        binding.power.setOnCheckedChangeListener(attrClickListener)
        binding.technical.setOnCheckedChangeListener(attrClickListener)
    }

    private val attrClickListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                attrList.add(buttonView.text.toString())
            }
            else {
                attrList.remove(buttonView.text.toString())
            }
            geneSelect()
        }
    }

    private val typeClickListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (buttonView == null) {
                return
            }
            if (isChecked) {
                typeList.add(buttonView.text.toString())
            }
            else {
                typeList.remove(buttonView.text.toString())
            }
            geneSelect()
        }
    }

    private fun geneSelect() {
        if (typeList.size == 0 && attrList.size == -0){
            if (this@ChooseGeneActivity::rainbowGene.isInitialized){
                adapter.addGeneToFirst(rainbowGene)
            }
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            val dao = GeneDataBase.getDatabase(this@ChooseGeneActivity).GeneDao()
            val geneList = dao.findByTypeAndAttributes(typeList, attrList)
            adapter.geneList = ArrayList<Gene>().apply {
                addAll(geneList)
            }
        }
    }

    override fun geneClick(gene: Gene) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("Gene", gene)
        })
        finish()
    }
}