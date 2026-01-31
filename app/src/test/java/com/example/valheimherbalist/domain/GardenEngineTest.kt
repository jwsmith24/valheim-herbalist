package com.example.valheimherbalist.domain

import androidx.lifecycle.GeneratedAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GardenEngineTest {



    private val carrot = Crop(
        id = "carrot",
        name = "Carrot",
        growDays = 2,
        harvestYield = 3
    )

    @Test
    fun `planting in empty plot consumes one seed and sets planted`() {
        val initial = GardenState(
            day = 0,
            plots = List(4) { PlotState.Empty},
            inventory = mapOf("carrot_seed" to 1)
        )

        val result = GardenEngine.plant(
            state = initial,
            plotIndex = 1,
            crop = carrot
        )

        assertTrue(result is PlantResult.PlantedSuccessfully)
        result as PlantResult.PlantedSuccessfully

        assertEquals(0, result.state.inventory["carrot_seed"] ?: 0)

        assertEquals(
            PlotState.Planted(cropId = "carrot", plantedDay = 0),
            result.state.plots[1]
        )
    }

    @Test
    fun `planting fails when there are no seeds`() {
        val initial = GardenState(
            day = 0,
            plots = List(2) { PlotState.Empty},
            inventory = emptyMap()
        )

        val result = GardenEngine.plant(initial, plotIndex = 0, crop = carrot)

        assertEquals(PlantResult.NotEnoughSeeds, result)
    }

    @Test
    fun `planting fails when plot is not empty`() {
        val initial = GardenState(
            day = 0,
            plots = listOf(
                PlotState.Planted("carrot", 0),
                PlotState.Empty
            ),
            inventory = mapOf("carrot_seed" to 1)
        )

        val result = GardenEngine.plant(initial, plotIndex = 0, crop = carrot)

        assertEquals(PlantResult.PlotNotEmpty, result)
    }

    @Test
    fun `advanceDay makes crop harvestable once growDays reached`() {
        val initial = GardenState(
            day = 0,
            plots = listOf(PlotState.Planted("carrot", plantedDay = 0)),
            inventory = emptyMap()
        )
        val crops = mapOf("carrot" to carrot)

        val day1 = GardenEngine.advanceDay(initial, crops)
        assertEquals(1, day1.day)
        assertEquals(PlotState.Planted("carrot", 0), day1.plots[0])

        val day2 = GardenEngine.advanceDay(day1, crops)
        assertEquals(2, day2.day)
        assertEquals(PlotState.Harvestable("carrot", 0), day2.plots[0])
    }

    @Test
    fun `harvesting plant that is not ready returns NotHarvestable`() {
        val initial = GardenState(
            day = 0,
            plots = listOf(PlotState.Planted(cropId = "carrot", plantedDay = 0)),
            inventory = emptyMap()
        )

        val result = GardenEngine.harvest(initial, 0)

        assertEquals(HarvestResult.NotHarvestable, result)
    }

    @Test
    fun `harvesting plant returns success state`() {
        val initial = GardenState(
            day = 0,
            plots = listOf(PlotState.Planted(cropId = "carrot", plantedDay = 0)),
            inventory = emptyMap()
        )

        val crops = mapOf("carrot" to carrot)

        val day1 = GardenEngine.advanceDay(initial, crops)
        val day2 = GardenEngine.advanceDay(day1, crops)

        val result = GardenEngine.harvest(day2, 0)

        val expectedState = day2.copy(
            plots = listOf(PlotState.Empty),
            inventory = mapOf("carrot" to 1)
        )

        assertEquals(HarvestResult.HarvestedSuccessfully(expectedState), result)
    }

}