package com.solinari.MHSGene

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GeneDao {
    @Query("SELECT * FROM Gene")
    suspend fun getAllGene(): List<Gene>

    @Query("SELECT * FROM GeneDetailInfo")
    suspend fun getAllGeneDetailInfo(): List<GeneDetailInfo>

    @Insert
    suspend fun insertGene(genes: List<Gene>)

    @Insert
    suspend fun insertGeneDetailInfo(genes: List<GeneDetailInfo>)

    @Query("SELECT * FROM Gene WHERE type In (:type) AND attributes In (:attributes)")
    suspend fun findByTypeAndAttributes(type: List<String>, attributes: List<String>): List<Gene>

    @Query("SELECT * FROM Gene WHERE id LIKE 1 limit 1")
    suspend fun getRainbowGene(): Gene

    @Query("SELECT * FROM GeneDetailInfo WHERE id LIKE (:id) limit 1")
    suspend fun getGeneDetailInfo(id: Int): GeneDetailInfo

    @Delete
    suspend fun deleteAllGene(genes: List<Gene>)

    @Delete
    suspend fun deleteAllGeneDetailInfo(genes: List<GeneDetailInfo>)
}