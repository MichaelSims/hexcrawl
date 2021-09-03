package sims.michael.hexcrawl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import sims.michael.hexcrawl.serde.IntRangeDeserializer

data class HexCrawlConfiguration(
    val terrainList: List<Terrain>,
    @JsonDeserialize(keyUsing = IntRangeDeserializer::class)
    val fillDirectionDieResultMap: Map<IntRange, FillDirection>,
    @JsonDeserialize(keyUsing = IntRangeDeserializer::class)
    val rarityDieResultMapTwoInputs: Map<IntRange, Rarity>,
    @JsonDeserialize(keyUsing = IntRangeDeserializer::class)
    val rarityDieResultMapThreeInputs: Map<IntRange, Rarity>
) {

    init {
        for (terrain in terrainList) {
            requireDieResultsMap(terrain.terrainDieResultMap)
        }
        requireDieResultsMap(fillDirectionDieResultMap)
        requireDieResultsMap(rarityDieResultMapTwoInputs)
        require(rarityDieResultMapTwoInputs.size == 2)
        requireDieResultsMap(rarityDieResultMapThreeInputs)
        require(rarityDieResultMapThreeInputs.size == 3)
    }

    private fun <T> requireDieResultsMap(resultsMap: Map<IntRange, T>) {
        requireContiguousRanges(resultsMap.keys)
        requireDistinctValues(resultsMap.values)
    }

    private fun <T> requireDistinctValues(iterable: Collection<T>) {
        require(iterable.size == iterable.distinct().size) {
            "Values are required to be distinct: $iterable"
        }
    }

    @JsonIgnore
    val idToTerrainMap: Map<String, Terrain> = terrainList.associateBy(Terrain::id)
}
