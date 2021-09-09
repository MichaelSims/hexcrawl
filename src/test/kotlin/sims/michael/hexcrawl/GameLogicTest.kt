package sims.michael.hexcrawl

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class GameLogicTest {

    @ParameterizedTest
    @MethodSource
    fun testRollForTerrain(
        dieResults: List<Int>,
        coordinateToLabelMap: Map<CubeCoordinate, String>,
        target: CubeCoordinate,
        expectedResult: String
    ) {
        val gameLogic = GameLogic(TestRandom(dieResults), config = TestConfiguration, originTerrainId = "Plains")
        val grid = MutableGrid().apply {
            for ((coordinate, label) in coordinateToLabelMap) {
                set(coordinate, label)
            }
        }
        val terrain = gameLogic.rollTerrainDie(grid, target)
        assertEquals(expectedResult, terrain.id)
    }

    @Test
    fun testSortTerrainListByRarity() {
        val gameLogic = GameLogic(config = TestConfiguration, originTerrainId = "Plains")
        val unsorted = listOf("Barrens", "Desert", "Tundra", "Plains")
        assertEquals(
            listOf("Plains", "Barrens", "Desert", "Tundra"),
            gameLogic.sortTerrainListByRarity(unsorted.mapNotNull { TestConfiguration.idToTerrainMap[it] })
                .map { it.id }
        )
    }

    companion object {
        @JvmStatic
        @Suppress("unused") // Used by JUnit
        fun testRollForTerrain() = listOf(
            // One adjacent hex
            Arguments.of(
                listOf(
                    0 // Plains die - 1
                ),
                mapOf(CubeCoordinate.ZERO to "Plains"), CubeCoordinate(0, -1),
                "Plains"
            ),

            // Two adjacent, but same type
            Arguments.of(
                listOf(
                    0 // Plains die - 1
                ),
                mapOf(
                    CubeCoordinate.ZERO to "Plains",
                    CubeCoordinate(1, -1) to "Plains"
                ),
                CubeCoordinate(0, -1), "Plains"
            ),

            // Two adjacent, two different types, uncommon result
            Arguments.of(
                listOf(
                    18, // Plains die - Hills
                    14, // Forest die - Mountains
                    0, // Rarity die - Uncommon -> Mountains
                ),
                mapOf(
                    CubeCoordinate.ZERO to "Plains",
                    CubeCoordinate(1, -1) to "Forest"
                ),
                CubeCoordinate(0, -1), "Mountains"
            ),

            // Three adjacent, two different types, uncommon result
            Arguments.of(
                listOf(
                    18, // Plains die - Hills
                    14, // Forest die - Mountains
                    0, // Rarity die - Uncommon -> Mountains
                ),
                mapOf(
                    CubeCoordinate.ZERO to "Plains",
                    CubeCoordinate(1, -1) to "Forest",
                    CubeCoordinate(-1, 0) to "Forest"
                ),
                CubeCoordinate(0, -1), "Mountains"
            ),

            // Three adjacent, three different types, uncommon result
            Arguments.of(
                listOf(
                    18, // Plains die - Hills
                    14, // Forest die - Mountains
                    19, // Tundra die - Barrens
                    0, // Rarity die - Uncommon -> Barrens
                ),
                mapOf(
                    CubeCoordinate.ZERO to "Plains",
                    CubeCoordinate(1, -1) to "Forest",
                    CubeCoordinate(-1, 0) to "Tundra"
                ),
                CubeCoordinate(0, -1), "Barrens"
            ),

            // Three adjacent, three different types, middle result
            Arguments.of(
                listOf(
                    18, // Plains die - Hills
                    14, // Forest die - Mountains
                    19, // Tundra die - Barrens
                    3, // Rarity die - Middle -> Mountains
                ),
                mapOf(
                    CubeCoordinate.ZERO to "Plains",
                    CubeCoordinate(1, -1) to "Forest",
                    CubeCoordinate(-1, 0) to "Tundra"
                ),
                CubeCoordinate(0, -1), "Mountains"
            ),

            // Three adjacent, three different types, common result
            Arguments.of(
                listOf(
                    18, // Plains die - Hills
                    14, // Forest die - Mountains
                    19, // Tundra die - Barrens
                    5, // Rarity die - Common -> Forest
                ),
                mapOf(
                    CubeCoordinate.ZERO to "Plains",
                    CubeCoordinate(1, -1) to "Forest",
                    CubeCoordinate(-1, 0) to "Tundra"
                ),
                CubeCoordinate(0, -1), "Hills"
            )
        )
    }
}
