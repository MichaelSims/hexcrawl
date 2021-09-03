package sims.michael.hexcrawl

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import sims.michael.hexcrawl.serde.IntRangeDeserializer

data class Terrain(
    val id: String, // For now, the id is also the label
    val textMapperSvgClass: String,
    @JsonDeserialize(keyUsing = IntRangeDeserializer::class)
    val terrainDieResultMap: Map<IntRange, String>
) // TODO rename
