package com.solinari.MHSGene

import android.app.Application
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

private const val GENE_LIST_URL = "https://bots.sang0.pw/mhs/ajax/data.json"

class MHGene : Application() {

    override fun onCreate() {
        super.onCreate()
        fetchGeneListData()
    }

    private fun fetchGeneListData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(GENE_LIST_URL)
            .get()

        client.newCall(request.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@MHGene, R.string.fetch_gene_error, Toast.LENGTH_LONG).show()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                if (response.body == null) {
                    Toast.makeText(this@MHGene, R.string.fetch_gene_error, Toast.LENGTH_LONG).show()
                    return
                }

                val respBody = response.body!!.string()

                val jsonElement = JsonParser().parse(respBody)
                if (jsonElement is JsonObject && jsonElement.has("version")) {

                    val sp = getSharedPreferences(MHGENE_SP, MODE_PRIVATE)
                    val dao = GeneDataBase.getDatabase(this@MHGene).GeneDao()

                    CoroutineScope(Dispatchers.Main).launch {
                        //若版本與本地的一樣，無需處理
                        if (sp.getString(GENE_LIST_VERSION_SP, "") != jsonElement.get("version").asString || dao.getAllGene().isEmpty()) {
                            dao.deleteAllGene(dao.getAllGene())
                            sp.edit().putString(GENE_LIST_VERSION_SP, jsonElement.get("version").asString).apply()
                            val geneArray = ArrayList<Gene>()

                            //彩虹因子
                            if (jsonElement.has("-1")) {
                                val rainbowGeneObject = jsonElement.getAsJsonObject("-1")
                                if (rainbowGeneObject.has("-1")) {
                                    val jsonArray = rainbowGeneObject.getAsJsonArray("-1")
                                    geneArray.add(geneParser(jsonArray.get(0).asJsonObject, -1, -1))
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

                            dao.insertGene(geneArray)
                        }
                    }
                }
                else {
                    Toast.makeText(this@MHGene, R.string.fetch_gene_error, Toast.LENGTH_LONG).show()
                }
            }
        })
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
}