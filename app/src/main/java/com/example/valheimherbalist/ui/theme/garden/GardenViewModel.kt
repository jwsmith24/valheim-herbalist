package com.example.valheimherbalist.ui.theme.garden

import androidx.lifecycle.ViewModel
import com.example.valheimherbalist.domain.Crop
import com.example.valheimherbalist.domain.GardenEngine
import com.example.valheimherbalist.domain.GardenState
import com.example.valheimherbalist.domain.HarvestResult
import com.example.valheimherbalist.domain.Inventory
import com.example.valheimherbalist.domain.PlantResult
import com.example.valheimherbalist.domain.PlotState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GardenViewModel: ViewModel() {

    private val carrot = Crop(
        id = "carrot",
        name = "Carrot",
        growDays = 2,
        harvestYield = 3
    )

    private val turnip = Crop(
        id = "turnip",
        name = "Turnip",
        growDays = 4,
        harvestYield = 3
    )

    private val cropsById = mapOf(
        carrot.id to carrot,
        turnip.id to turnip,
    )


    private val _uiState = MutableStateFlow(
        GardenUiState(
            garden = GardenState(
                day = 0,
                plots = List(STARTER_PLOT_SIZE) { PlotState.Empty},
                inventory = Inventory.EMPTY
                    .add("carrot_seed", STARTER_SEED_COUNT)
                    .add("turnip_seed", STARTER_SEED_COUNT)
            )

        )
    )

    val uiState: StateFlow<GardenUiState> = _uiState.asStateFlow()

    private val _activeCrop = MutableStateFlow(
        carrot
    )
    val activeCrop = _activeCrop.asStateFlow()

    fun nextDay() {
        val current = uiState.value.garden
        val advanced = GardenEngine.advanceDay(current, cropsById)

        _uiState.value = _uiState.value.copy(garden = advanced, message = null)
    }

    fun onPlotTapped(plotIndex: Int) {
        val current = uiState.value.garden

        when (val plot = current.plots[plotIndex]) {
            PlotState.Empty -> plantCrop(plotIndex, _activeCrop.value)
            is PlotState.Harvestable -> harvest(plotIndex)
            is PlotState.Planted -> _uiState.value = _uiState.value.copy(message = "Not ready yet!")
        }
    }

    fun onActiveCropTapped(cropId: String) {
        if (cropId !in cropsById || activeCrop.value == cropsById[cropId]){
            return
        } else _activeCrop.value = cropsById[cropId]!!

    }

    private fun plantCrop(plotIndex: Int, crop: Crop) {
        val current = _uiState.value.garden
        val result = GardenEngine.plant(current, plotIndex, crop)

        _uiState.value = when (result) {
            is PlantResult.PlantedSuccessfully -> GardenUiState(result.state, message = null)
            PlantResult.NotEnoughSeeds -> _uiState.value.copy(message = "No ${crop.name} seeds.")
            PlantResult.PlotNotEmpty -> _uiState.value.copy(message = "Plot is not empty.")
            PlantResult.InvalidPlotIndex -> _uiState.value.copy(message = "Invalid plot.")
        }
    }

    private fun harvest(plotIndex: Int) {
        val current = _uiState.value.garden
        val result = GardenEngine.harvest(current, plotIndex, cropsById)


        _uiState.value = when (result) {
            is HarvestResult.HarvestedSuccessfully -> GardenUiState(result.state, message = "Harvested!")
            HarvestResult.NotHarvestable -> _uiState.value.copy(message = "Not harvestable.")
            HarvestResult.PlotEmpty -> _uiState.value.copy(message = "Plot is empty.")
            HarvestResult.InvalidPlotIndex -> _uiState.value.copy(message = "Invalid plot.")
            is HarvestResult.UnknownCrop -> _uiState.value.copy(message = "Unknown crop: ${result.cropId}")
        }
    }




    companion object {
         const val STARTER_SEED_COUNT = 3
         const val STARTER_PLOT_SIZE = 9
    }
}