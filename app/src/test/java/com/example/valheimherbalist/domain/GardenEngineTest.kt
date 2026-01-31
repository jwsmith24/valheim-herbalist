package com.example.valheimherbalist.domain

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

    private val cropsById = mapOf(carrot.id to carrot)

    @Test
    fun `planting in empty plot consumes one seed and sets planted`() {
        val initial = GardenState(
            day = 0,
            plots = List(4) { PlotState.Empty},
            inventory = Inventory.EMPTY.add("carrot_seed", 1)
        )

        val result = GardenEngine.plant(
            state = initial,
            plotIndex = 1,
            crop = carrot
        )

        assertTrue(result is PlantResult.PlantedSuccessfully)
        result as PlantResult.PlantedSuccessfully

        assertEquals(0, result.state.inventory.count("carrot_seed"))

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
            inventory = Inventory.EMPTY
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
            inventory = Inventory.EMPTY.add("carrot_seed", 1)
        )

        val result = GardenEngine.plant(initial, plotIndex = 0, crop = carrot)

        assertEquals(PlantResult.PlotNotEmpty, result)
    }

    @Test
    fun `advanceDay makes crop harvestable once growDays reached`() {
        val initial = GardenState(
            day = 0,
            plots = listOf(PlotState.Planted("carrot", plantedDay = 0)),
            inventory = Inventory.EMPTY
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
            inventory = Inventory.EMPTY
        )

        val result = GardenEngine.harvest(initial, 0, cropsById)

        assertEquals(HarvestResult.NotHarvestable, result)
    }

    @Test
    fun `harvesting plant returns success state and updates inventory with correct yield`() {
        val initial = GardenState(
            day = 0,
            plots = listOf(PlotState.Planted(cropId = "carrot", plantedDay = 0)),
            inventory = Inventory.EMPTY
        )

        val crops = mapOf("carrot" to carrot)

        val day1 = GardenEngine.advanceDay(initial, crops)
        val day2 = GardenEngine.advanceDay(day1, crops)

        val result = GardenEngine.harvest(day2, 0, cropsById)

        val expectedState = day2.copy(
            plots = listOf(PlotState.Empty),
            inventory = Inventory.EMPTY.add("carrot", carrot.harvestYield)
        )

        assertEquals(HarvestResult.HarvestedSuccessfully(expectedState), result)
    }

    @Test
    fun `harvesting empty plot returns PlotEmpty`() {
        val initialState = GardenState(
            day = 0,
            plots = listOf(PlotState.Empty),
            inventory = Inventory.EMPTY
        )

        val result = GardenEngine.harvest(initialState, 0, cropsById)

        assertEquals(HarvestResult.PlotEmpty, result)
    }

    @Test
    fun `harvesting a plant missing from the crops list will return unkown plant with id`() {
        val initialState = GardenState(
            day = 0,
            plots = listOf(PlotState.Harvestable(
                cropId = "mystery-crop",
                plantedDay = 0
            )),
            inventory = Inventory.EMPTY
        )

        val result = GardenEngine.harvest(initialState, 0, cropsById)

        assertEquals(HarvestResult.UnknownCrop(cropId = "mystery-crop"), result)
    }

}