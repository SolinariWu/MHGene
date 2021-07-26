package com.solinari.MHSGene

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun hasGeneData(context: Context) =
    withContext(Dispatchers.IO) {
        val db = GeneDataBase.getDatabase(context)
        return@withContext db.GeneDao().getAllGene().isNotEmpty()
    }

suspend fun insertGeneData(context: Context, geneData: ArrayList<Gene>) =
    withContext(Dispatchers.IO) {
        val db = GeneDataBase.getDatabase(context)
        db.GeneDao().insertGene(geneData)
    }

suspend fun getAllGeneData(context: Context) =
    withContext(Dispatchers.IO) {
        val db = GeneDataBase.getDatabase(context)
        return@withContext db.GeneDao().getAllGene()
    }



suspend fun getGeneDataByAttr(context: Context, attr: List<String>) =
    withContext(Dispatchers.IO) {
        val db = GeneDataBase.getDatabase(context)
        return@withContext db.GeneDao().findByAttributes(attr)
    }

suspend fun getGeneDataByTypeAndAttr(context: Context, types: List<String>, attr: List<String>) =
    withContext(Dispatchers.IO) {
        val db = GeneDataBase.getDatabase(context)
        return@withContext db.GeneDao().findByTypeAndAttributes(types, attr)
    }

suspend fun getRainbowGene(context: Context) =
    withContext(Dispatchers.IO) {
        val db = GeneDataBase.getDatabase(context)
        return@withContext db.GeneDao().getRainbowGene()
    }