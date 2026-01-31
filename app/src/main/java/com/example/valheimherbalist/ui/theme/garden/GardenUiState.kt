package com.example.valheimherbalist.ui.theme.garden

import com.example.valheimherbalist.domain.GardenState

data class GardenUiState(
    val garden : GardenState,
    val message: String? = null
)
