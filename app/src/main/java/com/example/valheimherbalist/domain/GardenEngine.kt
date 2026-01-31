package com.example.valheimherbalist.domain

sealed interface PlantResult {
    data class PlantedSuccessfully(val state: GardenState): PlantResult
    data object PlotNotEmpty: PlantResult
    data object NotEnoughSeeds: PlantResult
    data object InvalidPlotIndex: PlantResult
}

sealed interface HarvestResult {

    data class HarvestedSuccessfully(val state: GardenState): HarvestResult
    data object NotHarvestable: HarvestResult
    data object PlotEmpty: HarvestResult
    data object InvalidPlotIndex: HarvestResult

    data class UnknownCrop(val cropId: String): HarvestResult
}

object GardenEngine {
    fun harvest(state: GardenState, plotIndex: Int, cropsById: Map<String, Crop>) : HarvestResult {
        if (plotIndex !in state.plots.indices) return HarvestResult.InvalidPlotIndex


        when(val targetPlot = state.plots[plotIndex]) {
            is PlotState.Empty -> return HarvestResult.PlotEmpty
            is PlotState.Planted -> return HarvestResult.NotHarvestable
            is PlotState.Harvestable -> {
                val yield: Int = cropsById[targetPlot.cropId]?.harvestYield ?: return HarvestResult.UnknownCrop(targetPlot.cropId)
                val updatedInventory = state.inventory
                    .add(targetPlot.cropId, yield)
                    .add("${targetPlot.cropId}_seed", (yield - 1).coerceAtLeast(1) )

                val updatedPlots = state.plots.toMutableList().also {
                    it[plotIndex] = PlotState.Empty
                }

                return HarvestResult.HarvestedSuccessfully(
                    state.copy(plots = updatedPlots, inventory = updatedInventory)
                )
            }
        }

    }
    fun plant(state: GardenState, plotIndex: Int, crop: Crop): PlantResult {

        if (plotIndex !in state.plots.indices) return PlantResult.InvalidPlotIndex

        val currentPlot = state.plots[plotIndex]
        if (currentPlot != PlotState.Empty) return PlantResult.PlotNotEmpty

        val seeds = state.inventory.count("${crop.id}_seed")
        if (seeds <= 0) return PlantResult.NotEnoughSeeds

        val updatedPlots = state.plots.toMutableList().apply {
            this[plotIndex] = PlotState.Planted(cropId = crop.id, plantedDay = state.day)
        }

        val updatedInventory = state.inventory.remove("${crop.id}_seed", 1)

        return PlantResult.PlantedSuccessfully(
            state.copy(plots = updatedPlots, inventory = updatedInventory)
        )
    }

    fun advanceDay(state: GardenState, cropsById: Map<String, Crop>): GardenState {
        val newDay = state.day + 1

        val updatedPlots = state.plots.map { plot ->
            when (plot) {
                is PlotState.Empty -> PlotState.Empty
                is PlotState.Harvestable -> plot
                is PlotState.Planted -> {
                    val crop = cropsById[plot.cropId] ?: return@map plot

                    val daysGrowing = newDay - plot.plantedDay
                    if (daysGrowing >= crop.growDays) {
                        PlotState.Harvestable(plot.cropId, plot.plantedDay)
                    } else {
                        plot
                    }
                }
            }
        }
        return state.copy(day = newDay, plots = updatedPlots)
    }
}