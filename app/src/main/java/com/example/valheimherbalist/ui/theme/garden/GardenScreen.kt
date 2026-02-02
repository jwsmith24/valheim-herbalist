package com.example.valheimherbalist.ui.theme.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.valheimherbalist.domain.CropType
import com.example.valheimherbalist.domain.PlotState


@Composable
fun CropToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedContainerColor = MaterialTheme.colorScheme.secondary
    val selectedContentColor = MaterialTheme.colorScheme.onSecondary

    val defaultContainerColor = MaterialTheme.colorScheme.surfaceVariant
    val defaultContentColor = MaterialTheme.colorScheme.onSurfaceVariant

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults
            .buttonColors(
                containerColor = if (isSelected) selectedContainerColor else defaultContainerColor,
                contentColor = if (isSelected) selectedContentColor else defaultContentColor
            )
    ) {
        Text(text)
    }
}

@Composable
fun GardenScreen(
    modifier: Modifier = Modifier,
    gardenViewModel: GardenViewModel = viewModel()
) {
    val uiState by gardenViewModel.uiState.collectAsState()

    val garden = uiState.garden
    val message = uiState.message


    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.background)

    ) {

        Text(text = "Valheim Herbalist",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
            )
        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text("Day: ${garden.day}")
            Button(onClick = { gardenViewModel.nextDay()}) {
                Text(text = "Next Day")
            }
        }

        Spacer(Modifier.height(12.dp))

        // inventory quick view
        Text(text = "Inventory", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(6.dp))
        Column (Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 10.dp, horizontal = 16.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Carrot Seeds: ${garden.inventory.count("carrot_seed")}")
                    Text(text = "Carrots: ${garden.inventory.count("carrot")}")
                }
                Column {
                    Text(text = "Turnip Seeds: ${garden.inventory.count("turnip_seed")}")
                    Text(text = "Turnips: ${garden.inventory.count("turnip")}")
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(text = message ?: "", color = MaterialTheme.colorScheme.primary)


        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Garden", style = MaterialTheme.typography.headlineMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CropToggleButton(
                    onClick = { gardenViewModel.onActiveCropTapped(CropType.CARROT) },
                    text = "Carrot",
                    isSelected = garden.activeCrop.id == "carrot"

                )
                CropToggleButton(
                    onClick = { gardenViewModel.onActiveCropTapped(CropType.TURNIP) },
                    text = "Turnip",
                    isSelected = garden.activeCrop.id == "turnip"

                )
            }
        }

        Spacer(Modifier.height(8.dp))


        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(10.dp),
            contentAlignment = Alignment.Center,

        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),

                contentPadding = PaddingValues(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                ,


            ) {
                itemsIndexed(garden.plots) {index, plot ->
                    val label = when (plot) {
                        PlotState.Empty -> "Empty"
                        is PlotState.Planted -> "Growing"
                        is PlotState.Harvestable -> "Ready"
                    }

                    Button(
                        onClick = {gardenViewModel.onPlotTapped(index)},
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                    ) {Text(text = label) }
                }

            }
        }
    }
}