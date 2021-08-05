package com.solinari.MHSGene

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.solinari.MHSGene.databinding.ActivityMainBinding
import io.github.hyuwah.draggableviewlib.DraggableListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), DraggableListener, listenere {
    private lateinit var binding: ActivityMainBinding
    private var wantGeneList = ArrayList<Gene>()
    private var geneViewList = ArrayList<View>()
    private var geneIconList = ArrayList<ImageView>()
    private var geneDeleteIconList = ArrayList<ImageView>()
    private var geneNameList = ArrayList<TextView>()
    private var wantGenePositionList = ArrayList<ViewPosition>()
    private var wantGeneViewList = ArrayList<ImageView>()
    private var geneList = arrayOf(Gene(), Gene(), Gene(), Gene(), Gene(), Gene(), Gene(), Gene(), Gene())
    private var ar = arrayOf(
        arrayOf(0, 1, 2), arrayOf(3, 4, 5), arrayOf(6, 7, 8),
        arrayOf(0, 3, 6), arrayOf(1, 4, 7), arrayOf(2, 5, 8),
        arrayOf(0, 4, 8), arrayOf(2, 4, 6)
    )
    private var canChoose = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initGeneViewList()
        initGeneIconList()
        initGeneDeleteIconList()
        initGeneNameList()
        initWantGeneList()
        setAbout()
        addWantGene()
        checkCanChoose()
    }

    override fun onPositionChanged(view: View) {

    }

    private fun initGeneNameList() {
        geneNameList.add(binding.oneGeneName)
        geneNameList.add(binding.twoGeneName)
        geneNameList.add(binding.threeGeneName)
        geneNameList.add(binding.fourGeneName)
        geneNameList.add(binding.fiveGeneName)
        geneNameList.add(binding.sixGeneName)
        geneNameList.add(binding.sevenGeneName)
        geneNameList.add(binding.eightGeneName)
        geneNameList.add(binding.nineGeneName)
    }

    private fun initGeneViewList() {
        geneViewList.add(binding.oneGene)
        geneViewList.add(binding.twoGene)
        geneViewList.add(binding.threeGene)
        geneViewList.add(binding.fourGene)
        geneViewList.add(binding.fiveGene)
        geneViewList.add(binding.sixGene)
        geneViewList.add(binding.sevenGene)
        geneViewList.add(binding.eightGene)
        geneViewList.add(binding.nineGene)
        geneViewList.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                if (geneList[index].id > 0) {
                    val reportDialog = GeneDetailInfoDialog.newInstance(geneList[index])
                    reportDialog.show(supportFragmentManager, GENE_DETAIL_INFO_DIALOG)
                }
            }
        }
    }

    private fun initGeneIconList() {
        geneIconList.add(binding.oneGeneIcon)
        geneIconList.add(binding.twoGeneIcon)
        geneIconList.add(binding.threeGeneIcon)
        geneIconList.add(binding.fourGeneIcon)
        geneIconList.add(binding.fiveGeneIcon)
        geneIconList.add(binding.sixGeneIcon)
        geneIconList.add(binding.sevenGeneIcon)
        geneIconList.add(binding.eightGeneIcon)
        geneIconList.add(binding.nineGeneIcon)
    }

    private fun initGeneDeleteIconList() {
        geneDeleteIconList.add(binding.oneGeneDeleteIcon)
        geneDeleteIconList.add(binding.twoGeneDeleteIcon)
        geneDeleteIconList.add(binding.threeGeneDeleteIcon)
        geneDeleteIconList.add(binding.fourGeneDeleteIcon)
        geneDeleteIconList.add(binding.fiveGeneDeleteIcon)
        geneDeleteIconList.add(binding.sixGeneDeleteIcon)
        geneDeleteIconList.add(binding.sevenGeneDeleteIcon)
        geneDeleteIconList.add(binding.eightGeneDeleteIcon)
        geneDeleteIconList.add(binding.nineGeneDeleteIcon)
        geneDeleteIconList.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                geneIconList[index].setImageResource(R.drawable.ic_baseline_brightness_1_24)
                geneNameList[index].text = ""
                geneList[index] = Gene()
                checkLine()
            }
        }
    }

    private fun initWantGeneList() {
        wantGeneViewList.add(binding.wantGeneOne)
        wantGeneViewList.add(binding.wantGeneTwo)
        wantGeneViewList.add(binding.wantGeneThree)
        wantGeneViewList.add(binding.wantGeneFour)
        wantGeneViewList.add(binding.wantGeneFive)
        binding.wantGeneOne.setOnClickListener { wantGeneClick(0) }
        binding.wantGeneTwo.setOnClickListener { wantGeneClick(1) }
        binding.wantGeneThree.setOnClickListener { wantGeneClick(2) }
        binding.wantGeneFour.setOnClickListener { wantGeneClick(3) }
        binding.wantGeneFive.setOnClickListener { wantGeneClick(4) }

        binding.root.post {
            wantGenePositionList.add(ViewPosition(binding.wantGeneOne.x, binding.wantGeneOne.y))
            wantGenePositionList.add(ViewPosition(binding.wantGeneTwo.x, binding.wantGeneTwo.y))
            wantGenePositionList.add(ViewPosition(binding.wantGeneThree.x, binding.wantGeneThree.y))
            wantGenePositionList.add(ViewPosition(binding.wantGeneFour.x, binding.wantGeneFour.y))
            wantGenePositionList.add(ViewPosition(binding.wantGeneFive.x, binding.wantGeneFive.y))
        }
    }

    private fun setAbout() {
        try {
            binding.about.setOnClickListener { startActivity(Intent(this, AboutActivity::class.java)) }
            val versionName = BuildConfig.VERSION_NAME
            binding.about.text = getString(R.string.about, versionName)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    private fun wantGeneClick(position: Int) {
        if (position >= wantGeneList.size) {
            if (canChoose) {
                activityResult.launch(Intent(this, ChooseGeneActivity::class.java))
            }
            else {
                Toast.makeText(this, R.string.fetch_gene_process, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addWantGene() {
        wantGeneViewList.forEachIndexed { index, imageView ->
            if (wantGeneList.size > index) {
                imageView.setImageResource(wantGeneList[index].getGeneIcon())
                imageView.setupGeneDraggable(listener = this)
            }
            else {
                imageView.setImageResource(R.drawable.ic_baseline_add_24)
                imageView.setOnTouchListener(null)
            }
        }
    }

    private fun checkCanChoose() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = GeneDataBase.getDatabase(this@MainActivity).GeneDao()
            while (dao.getAllGene().isEmpty()) {
                canChoose = false
            }
            canChoose = true
        }
    }

    override fun dragEnd(v: View) {
        val dragGeneListPosition = v.tag.toString().toInt()
        if (dragGeneListPosition >= wantGeneList.size) {
            v.x = wantGenePositionList[dragGeneListPosition].x
            v.y = wantGenePositionList[dragGeneListPosition].y
            return
        }
        val gene = wantGeneList[dragGeneListPosition]
        for ((index, view) in geneViewList.withIndex()) {
            if (v.x > view.x && v.x < view.x + view.width &&
                v.y > view.y && v.y < view.y + view.height
            ) {
                geneIconList[index].setImageResource(gene.getGeneIcon())
                geneNameList[index].text = gene.name
                (v as ImageView).setImageResource(0)
                v.x = wantGenePositionList[dragGeneListPosition].x
                v.y = wantGenePositionList[dragGeneListPosition].y
                geneList[index] = wantGeneList[dragGeneListPosition]
                wantGeneList.removeAt(dragGeneListPosition)
                checkLine()
                addWantGene()
                return
            }
        }
        v.x = wantGenePositionList[dragGeneListPosition].x
        v.y = wantGenePositionList[dragGeneListPosition].y
    }

    private var activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data?.getParcelableExtra<Gene>("Gene") != null) {
            val gene = it.data!!.getParcelableExtra<Gene>("Gene")!!
            var isDouble = false
            geneList.forEach {
                if (it.id == gene.id) {
                    isDouble = true
                    return@forEach
                }
            }

            if (isDouble) {
                Toast.makeText(this, R.string.gene_double, Toast.LENGTH_SHORT).show()
            }
            else {
                val addPosition = wantGeneList.size
                wantGeneList.add(addPosition, gene)
                addWantGene()
            }
        }
    }

    private fun checkLine() {
        var nonLine = 0
        var fireLine = 0
        var waterLine = 0
        var thunderLine = 0
        var iceLine = 0
        var dragonLine = 0
        var powerLine = 0
        var speedLine = 0
        var technicalLine = 0

        ar.forEach {
            //三個同樣屬性
            if (geneList[it[0]].type == geneList[it[1]].type && geneList[it[0]].type == geneList[it[2]].type) {
                when (geneList[it[0]].type) {
                    "無" -> nonLine += 1
                    "火" -> fireLine += 1
                    "水" -> waterLine += 1
                    "雷" -> thunderLine += 1
                    "冰" -> iceLine = +1
                    "龍" -> dragonLine += 1
                }
            }

            //三個同樣猜拳
            if (geneList[it[0]].attributes == geneList[it[1]].attributes && geneList[it[0]].attributes == geneList[it[2]].attributes) {
                when (geneList[it[0]].attributes) {
                    "力量" -> powerLine += 1
                    "速度" -> speedLine += 1
                    "技巧" -> technicalLine += 1
                }
            }

            //檢查屬性連線虹色
            if (geneList[it[0]].type == "全" || geneList[it[1]].type == "全" || geneList[it[2]].type == "全") {
                if (geneList[it[0]].type == geneList[it[1]].type ||
                    geneList[it[1]].type == geneList[it[2]].type ||
                    geneList[it[0]].type == geneList[it[2]].type
                ) {


                    when {
                        geneList[it[0]].type == "全" -> {
                            when (geneList[it[1]].type) {
                                "無" -> nonLine += 1
                                "火" -> fireLine += 1
                                "水" -> waterLine += 1
                                "雷" -> thunderLine += 1
                                "冰" -> iceLine = +1
                                "龍" -> dragonLine += 1
                            }
                        }
                        geneList[it[1]].type == "全" -> {
                            when (geneList[it[2]].type) {
                                "無" -> nonLine += 1
                                "火" -> fireLine += 1
                                "水" -> waterLine += 1
                                "雷" -> thunderLine += 1
                                "冰" -> iceLine = +1
                                "龍" -> dragonLine += 1
                            }
                        }
                        else -> {
                            when (geneList[it[0]].type) {
                                "無" -> nonLine += 1
                                "火" -> fireLine += 1
                                "水" -> waterLine += 1
                                "雷" -> thunderLine += 1
                                "冰" -> iceLine = +1
                                "龍" -> dragonLine += 1
                            }
                        }
                    }

                }
            }

            //檢查猜拳連線虹色
            if (geneList[it[0]].attributes == "全" || geneList[it[1]].attributes == "全" || geneList[it[2]].attributes == "全") {
                if (geneList[it[0]].attributes == geneList[it[1]].attributes ||
                    geneList[it[1]].attributes == geneList[it[2]].attributes ||
                    geneList[it[0]].attributes == geneList[it[2]].attributes
                ) {

                    when {
                        geneList[it[0]].type == "全" -> {
                            when (geneList[it[1]].attributes) {
                                "力量" -> powerLine += 1
                                "速度" -> speedLine += 1
                                "技巧" -> technicalLine += 1
                            }
                        }
                        geneList[it[1]].type == "全" -> {
                            when (geneList[it[2]].attributes) {
                                "力量" -> powerLine += 1
                                "速度" -> speedLine += 1
                                "技巧" -> technicalLine += 1
                            }
                        }
                        else -> {
                            when (geneList[it[0]].attributes) {
                                "力量" -> powerLine += 1
                                "速度" -> speedLine += 1
                                "技巧" -> technicalLine += 1
                            }
                        }
                    }
                }
            }
        }

        binding.nonPercent.text = getString(R.string.line_percent, lineToPercent(nonLine))
        binding.firePercent.text = getString(R.string.line_percent, lineToPercent(fireLine))
        binding.waterPercent.text = getString(R.string.line_percent, lineToPercent(waterLine))
        binding.thunderPercent.text = getString(R.string.line_percent, lineToPercent(thunderLine))
        binding.icePercent.text = getString(R.string.line_percent, lineToPercent(iceLine))
        binding.dragonPercent.text = getString(R.string.line_percent, lineToPercent(dragonLine))
        binding.powerPercent.text = getString(R.string.line_percent, lineToPercent(powerLine))
        binding.speedPercent.text = getString(R.string.line_percent, lineToPercent(speedLine))
        binding.technicalPercent.text = getString(R.string.line_percent, lineToPercent(technicalLine))
    }

    private fun lineToPercent(lineCount: Int): Int {
        var percent = 100
        for (i in 0 until lineCount) {
            if (i < 2) {
                percent += 10
            }
            else {
                percent += 5
            }
        }
        return percent
    }
}