package com.example.valheimherbalist.domain

data class Crop(
    val id: String,
    val name: String,
    val growDays: Int,
    val harvestYield: Int
)


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
    val inventory: Inventory
)