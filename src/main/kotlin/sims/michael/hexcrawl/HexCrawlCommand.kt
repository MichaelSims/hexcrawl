package sims.michael.hexcrawl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.clikt.sources.PropertiesValueSource
import com.github.ajalt.clikt.sources.ValueSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sims.michael.hexcrawl.render.GridRenderer
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class HexCrawlCommand : CliktCommand() {

    private val logger = LoggerFactory.getLogger(HexCrawlCommand::class.java)

    init {
        context {
            helpFormatter = CliktHelpFormatter(showDefaultValues = true)
            valueSource = fromPropertiesFileInPwd()
        }
    }

    private val pwd: File = File(requireNotNull(System.getProperty("user.dir")))

    private fun fromPropertiesFileInPwd(): ValueSource? {
        val propertiesFile = File(pwd, "hexcrawl.cfg")
        return if (propertiesFile.exists()) {
            logger.info("Reading config from ${propertiesFile.absolutePath}")
            PropertiesValueSource.from(propertiesFile)
        } else {
            logger.warn("No config file found at ${propertiesFile.absolutePath}")
            null
        }
    }

    private val pathLength: Int by option(help = "The link of the path the player will take, in hexes")
        .int()
        .default(32)
        .check(validator = { it > 0 }, lazyMessage = { "$it is not a positive integer" })

    private val configFile: File by option(help = "JSON configuration file")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
        .default(File(pwd, "config.json"))

    private val startingTerrain by option(help = "The id/label of the terrain type to assign to the origin hex")
        .required()

    private val randomSeed by option(help = "Long value to use as a PRNG seed for die rolls").long()

    private val gameLogic: GameLogic by lazy {
        if (randomSeed != null) {
            logger.debug("Using $randomSeed as PRNG seed value")
        }
        GameLogic(
            random = Random.javaUtilRandom(randomSeed),
            config = config,
            originTerrainId = startingTerrain
        )
    }

    private val config: HexCrawlConfiguration by lazy {
        ObjectMapper()
            .registerModule(KotlinModule())
            .readValue(configFile, HexCrawlConfiguration::class.java)
    }

    private val rendererId by option(help = "The ID of the renderer to use.")
        .enum(key = GridRenderer.RendererId::optionName)
        .default(GridRenderer.RendererId.Ascii)

    private val numMapsToCreate by option(help = "The number of maps to render.")
        .int()
        .default(1)
        .check { it > 0 }

    override fun run() {
        logEffectiveConfig(logger)

        val outputDirectory = File("maps_${getTimestampDirName()}").apply { mkdir() }

        val gridRenderer = GridRenderer.getRendererById(rendererId, config)

        for (sequenceNumber in 0 until numMapsToCreate) {

            val path: Deque<CubeCoordinate> = ArrayDeque(CubeCoordinate.ZERO.getSpiralPath().take(pathLength).toList())

            val grid = MutableGrid().apply {
                for (pathPoint in path) {
                    for (point in listOf(pathPoint) + pathPoint.getAdjacentPoints(gameLogic.rollFillDirectionDie())) {
                        if (get(point) == null) {
                            logger.debug("Rolling terrain for ${point.toOffsetCoordinate()}")
                            set(point, gameLogic.rollTerrainDie(this, point).id)
                        }
                    }
                }
            }

            gridRenderer.renderToFile(grid, outputDirectory, "%03d".format(sequenceNumber))
        }

        logger.info("Files written to $outputDirectory")
        ProcessBuilder()
            .command("open", outputDirectory.absolutePath)
            .start()
    }

    private fun logEffectiveConfig(logger: Logger) {
        for ((name, value) in getEffectiveConfig()) {
            logger.info("CONFIG $name: $value")
        }
    }

    private fun getEffectiveConfig(): Map<String, Any> {
        // Clikt doesn't seem to offer a nice way to dump the effective config, so we have to make some assumptions here
        return registeredOptions()
            .mapNotNull { option ->
                val optionName = option.names.joinToString(",")
                val optionValue = when (option) {
                    is OptionWithValues<*, *, *> -> option.value
                    is FlagOption<*> -> option.value
                    else -> null
                }
                optionValue?.let { optionName to it }
            }
            .toMap()
    }

    private fun getTimestampDirName() =
        LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME).replace("[:.]".toRegex(), "_")

}
