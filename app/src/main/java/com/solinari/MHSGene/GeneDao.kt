package com.solinari.MHSGene

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GeneDao {
    @Query("SELECT * FROM Gene")
    fun getAllGene(): List<Gene>

    @Insert
    fun insertGene(genes: List<Gene>)

    @Query("SELECT * FROM Gene WHERE type In (:type)")
    fun findByType(type: List<String>): List<Gene>

    @Query("SELECT * FROM Gene WHERE type In (:attributes)")
    fun findByAttributes(attributes: List<String>): List<Gene>

    @Query("SELECT * FROM Gene WHERE type In (:type) AND attributes In (:attributes)")
    fun findByTypeAndAttributes(type: List<String>, attributes: List<String>): List<Gene>

    @Query("SELECT * FROM Gene WHERE id LIKE 1 limit 1")
    fun getRainbowGene(): Gene
}