package sims.michael.hexcrawl

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GameLogic(
    private val random: Random = Random.javaUtilRandom(),
    private val config: HexCrawlConfiguration,
    originTerrainId: String
) {

    private val logger: Logger = LoggerFactory.getLogger(GameLogic::class.java)

    private val originTerrain = checkNotNull(config.idToTerrainMap[originTerrainId])
    private val terrainRarityIndexById =
        config.idToTerrainMap.keys.withIndex().associate { it.value to it.index }


    fun rollTerrainDie(mutableGrid: MutableGrid, coordinate: CubeCoordinate): Terrain {
        val adjacentTerrains = coordinate.getAdjacentPoints()
            .mapNotNull { adjacentPoint -> mutableGrid.get(adjacentPoint) }
            .map { label -> checkNotNull(config.idToTerrainMap[label]) }
            .distinct()
            .let(::sortTerrainListByRarity)
            .takeLast(3)
        return if (adjacentTerrains.isEmpty()) {
            logger.debug("No adjacent terrains, returning origin ${originTerrain.id}")
            originTerrain
        } else {
            rollForTerrain(adjacentTerrains)
        }
    }

    private fun rollForTerrain(adjacentTerrains: List<Terrain>): Terrain {
        require(adjacentTerrains.size in 1..3)

        val rollResults = adjacentTerrains.associateWith(::rollTerrainDie)

        fun chooseResultByRarityRoll(rollId: String, dieResultsMap: Map<IntRange, Rarity>): Terrain {
            val rarity = rollDie(rollId, dieResultsMap)
            val chosenIndex = dieResultsMap.values.sorted().indexOf(rarity)
            val (terrainDie, terrainResult) = rollResults.entries.toList()[chosenIndex]
            logger.debug("Choosing $rarity ${terrainDie.id} result (${terrainResult.id}) from dice ${rollResults.keys.map { it.id }}")
            return terrainResult
        }

        return when (rollResults.size) {
            1 -> rollResults.values.first()
            2 -> chooseResultByRarityRoll("rarity two inputs", config.rarityDieResultMapTwoInputs)
            3 -> chooseResultByRarityRoll("rarity three inputs", config.rarityDieResultMapThreeInputs)
            else -> throw IllegalStateException()
        }
    }

    fun rollFillDirectionDie(): FillDirection = rollDie("fill direction", config.fillDirectionDieResultMap)

    private fun rollTerrainDie(terrain: Terrain): Terrain =
        checkNotNull(config.idToTerrainMap[rollDie(terrain.id, terrain.terrainDieResultMap)])

    private fun <T> rollDie(id: String, dieResultsMap: Map<IntRange, T>): T {
        val dieSize = dieResultsMap.keys.mergeRanges().let { merged -> merged.last - merged.first + 1 }
        val dieRoll = random.nextInt(dieSize) + 1
        return dieResultsMap
            .firstNotNullOf { (resultRange, result) -> if (dieRoll in resultRange) result else null }
            .also { logger.debug("DIE ROLL for $id: $dieRoll (of $dieSize) -> $it") }
    }

    fun sortTerrainListByRarity(unsortedTerrain: List<Terrain>): List<Terrain> =
        unsortedTerrain.sortedBy { terrainRarityIndexById[it.id] }
}
