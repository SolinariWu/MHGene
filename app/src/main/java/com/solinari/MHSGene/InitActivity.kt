package com.solinari.MHSGene

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.solinari.MHSGene.databinding.ActivityInitBinding
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

private const val GENE_DATA_URL = "https://bots.sang0.pw/mhs/ajax/data.json"

class InitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitBinding
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        scope.launch(exceptionHandler) {
            checkGeneData()
        }
    }

    private suspend fun checkGeneData() {
        if (hasGeneData(applicationContext)) {
            scope.cancel()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else {
            fetchGeneData()
        }
    }

    private fun geneParser(jsonObject: JsonObject, geneType: Int, gentAttributes: Int): Gene {
        return Gene().apply {
            id = jsonObject.get("id").asInt
            type = jsonObject.get("type").asString
            attributes = jsonObject.get("attributes").asString
            name = jsonObject.get("name").asString
            geneClass = when (geneType) {
                1 -> {
                    when (gentAttributes) {
                        1 -> {
                            GeneClass.NonPower
                        }
                        2 -> {
                            GeneClass.NonTechnical
                        }
                        3 -> {
                            GeneClass.NonSpeed
                        }
                        4 -> {
                            GeneClass.NonEle
                        }
                        else -> throw  IllegalArgumentException()
                    }
                }
                2 -> {
                    when (gentAttributes) {
                        1 -> {
                            GeneClass.FirePower
                        }
                        2 -> {
                            GeneClass.FireTechnical
                        }
                        3 -> {
                            GeneClass.FireSpeed
                        }
                        4 -> {
                            GeneClass.FireEle
                        }
                        else -> throw  IllegalArgumentException()
                    }
                }
                3 -> {
                    when (gentAttributes) {
                        1 -> {
                            GeneClass.WaterPower
                        }
                        2 -> {
                            GeneClass.WaterTechnical
                        }
                        3 -> {
                            GeneClass.WaterSpeed
                        }
                        4 -> {
                            GeneClass.WaterEle
                        }
                        else -> throw  IllegalArgumentException()
                    }
                }
                4 -> {
                    when (gentAttributes) {
                        1 -> {
                            GeneClass.ThunderPower
                        }
                        2 -> {
                            GeneClass.ThunderTechnical
                        }
                        3 -> {
                            GeneClass.ThunderSpeed
                        }
                        4 -> {
                            GeneClass.ThunderEle
                        }
                        else -> throw  IllegalArgumentException()
                    }
                }
                5 -> {
                    when (gentAttributes) {
                        1 -> {
                            GeneClass.IcePower
                        }
                        2 -> {
                            GeneClass.IceTechnical
                        }
                        3 -> {
                            GeneClass.IceSpeed
                        }
                        4 -> {
                            GeneClass.IceEle
                        }
                        else -> throw  IllegalArgumentException()
                    }
                }
                6 -> {
                    when (gentAttributes) {
                        1 -> {
                            GeneClass.DragonPower
                        }
                        2 -> {
                            GeneClass.DragonTechnical
                        }
                        3 -> {
                            GeneClass.DragonSpeed
                        }
                        4 -> {
                            GeneClass.DragonEle
                        }
                        else -> throw  IllegalArgumentException()
                    }
                }
                -1 -> GeneClass.Rainbow
                else -> throw  IllegalArgumentException()
            }
        }
    }

    private fun geneTypeParser(jsonObject: JsonObject, type: Int): ArrayList<Gene> {
        val geneArray = ArrayList<Gene>()
        //力量
        if (jsonObject.has("1")) {
            val nonPowerGeneArray = jsonObject.getAsJsonArray("1")
            nonPowerGeneArray.forEach {
                geneArray.add(geneParser(it.asJsonObject, type, 1))
            }
        }

        //技巧
        if (jsonObject.has("2")) {
            val nonTechnicalGeneArray = jsonObject.getAsJsonArray("2")
            nonTechnicalGeneArray.forEach {
                geneArray.add(geneParser(it.asJsonObject, type, 2))
            }
        }

        //速度
        if (jsonObject.has("3")) {
            val nonSpeedGeneArray = jsonObject.getAsJsonArray("3")
            nonSpeedGeneArray.forEach {
                geneArray.add(geneParser(it.asJsonObject, type, 3))
            }
        }

        //無
        if (jsonObject.has("4")) {
            val nonGeneArray = jsonObject.getAsJsonArray("4")
            nonGeneArray.forEach {
                geneArray.add(geneParser(it.asJsonObject, type, 4))
            }
        }

        return geneArray
    }

    private fun fetchGeneData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(GENE_DATA_URL)
            .get()

        client.newCall(request.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                if (response.body == null) {
                    finish()
                    return
                }

                val respBody = response.body!!.string()

                val jsonElement = JsonParser().parse(respBody)
                if (jsonElement is JsonObject) {
                    val geneArray = ArrayList<Gene>()

                    //彩虹因子
                    if (jsonElement.has("-1")) {
                        val rainbowGeneObject = jsonElement.getAsJsonObject("-1")
                        if (rainbowGeneObject.has("-1")) {
                            val jsonArray = rainbowGeneObject.getAsJsonArray("-1")
                            geneArray.add(geneParser(jsonArray.get(0).asJsonObject,-1,-1))
                        }
                    }

                    //無屬
                    if (jsonElement.has("1")) {
                        geneArray.addAll(geneTypeParser(jsonElement.getAsJsonObject("1"), 1))
                    }

                    //火屬
                    if (jsonElement.has("2")) {
                        geneArray.addAll(geneTypeParser(jsonElement.getAsJsonObject("2"), 2))
                    }

                    //水屬
                    if (jsonElement.has("3")) {
                        geneArray.addAll(geneTypeParser(jsonElement.getAsJsonObject("3"), 3))
                    }

                    //雷屬
                    if (jsonElement.has("4")) {
                        geneArray.addAll(geneTypeParser(jsonElement.getAsJsonObject("4"), 4))
                    }

                    //冰屬
                    if (jsonElement.has("5")) {
                        geneArray.addAll(geneTypeParser(jsonElement.getAsJsonObject("5"), 5))
                    }

                    //龍屬
                    if (jsonElement.has("6")) {
                        geneArray.addAll(geneTypeParser(jsonElement.getAsJsonObject("6"), 6))
                    }

                    scope.launch(exceptionHandler) {
                        fetchGeneDataComplete(geneArray)
                    }
                }
            }
        })
    }

    private suspend fun fetchGeneDataComplete(geneArray: ArrayList<Gene>) {
        insertGeneData(applicationContext, geneArray)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Toast.makeText(this, "載入基因錯誤，${throwable.message}", Toast.LENGTH_SHORT).show()
    }
}