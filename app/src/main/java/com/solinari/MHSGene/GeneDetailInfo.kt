package com.solinari.MHSGene

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class GeneDetailInfo(
    @PrimaryKey var id: Int = 0,
    @ColumnInfo var name: String = "",
    @ColumnInfo var skill:String = "",
    @ColumnInfo var type: String = "",
    @ColumnInfo var cost: String = "",
    @ColumnInfo var desc: String = "",
    @ColumnInfo var from: String = ""
) : Parcelable