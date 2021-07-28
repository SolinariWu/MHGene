package com.solinari.MHSGene

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATA_BASE_NAME = "gene_database"

@Database(entities = arrayOf(Gene::class, GeneDetailInfo::class), version = 3)
abstract class GeneDataBase : RoomDatabase() {
    abstract fun GeneDao(): GeneDao

    companion object {
        @Volatile
        private var INSTANCE: GeneDataBase? = null

        fun getDatabase(context: Context): GeneDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GeneDataBase::class.java,
                    DATA_BASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
