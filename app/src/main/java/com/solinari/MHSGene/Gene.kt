package com.solinari.MHSGene

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class GeneClass {
    NonEle, NonPower, NonSpeed, NonTechnical,
    FireEle, FirePower, FireSpeed, FireTechnical,
    WaterEle, WaterPower, WaterSpeed, WaterTechnical,
    ThunderEle, ThunderPower, ThunderSpeed, ThunderTechnical,
    IceEle, IcePower, IceSpeed, IceTechnical,
    DragonEle, DragonPower, DragonSpeed, DragonTechnical,
    Rainbow
}

@Entity
@Parcelize
data class Gene(
    @PrimaryKey var id: Int = 0,
    @ColumnInfo var type: String = "",
    @ColumnInfo var attributes: String = "",
    @ColumnInfo var name: String = "",
    @ColumnInfo var geneClass: GeneClass = GeneClass.NonEle
) : Parcelable