package com.example.valheimherbalist.domain

import com.example.valheimherbalist.ui.theme.garden.GardenViewModel

data class Crop(
    val id: String,
    val name: String,
    val growDays: Int,
    val harvestYield: Int
)

enum class CropType(val id: String, val label: String) {
    CARROT("carrot", "Carrot"),
    TURNIP("turnip", "Turnip");

    companion object {
        fun fromId(id: String): CropType? = entries.firstOrNull {it.id == id}
    }
}

sealed interface PlotState {
    data object Empty: PlotState

    data class Planted(
        val cropId: String,
        val plantedDay: Int
    ) : PlotState

    data class Harvestable(
        val cropId: String,
        val plantedDay: Int
    ): PlotState
}

data class GardenState (
    val day: Int,
    val plots: List<PlotState>,
    val inventory: Inventory,
    val activeCrop: CropType
)