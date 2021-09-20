package sims.michael.hexcrawl.render

import sims.michael.hexcrawl.CubeCoordinate
import sims.michael.hexcrawl.HexCrawlConfiguration
import sims.michael.hexcrawl.MutableGrid
import sims.michael.hexcrawl.toOffsetCoordinate
import java.io.File

class TextMapperRenderer(private val config: HexCrawlConfiguration): GridRenderer {
    override fun renderToFile(grid: MutableGrid, parentDirectory: File, fileNumber: String): List<File> {
        val renderSb = StringBuilder()
        val translated = grid.translate(-grid.qRange.first, -grid.rRange.first)
        for (q in translated.qRange) {
            for (r in translated.rRange) {
                val coordinate = CubeCoordinate(q, r)
                val terrain = translated.get(coordinate)?.let { config.idToTerrainMap[it] }
                if (terrain != null) {
                    val (offsetQ, offsetR) = coordinate.toOffsetCoordinate()
                    renderSb.appendLine(
                        "%02d%02d %s \"%s\"".format(
                            offsetQ,
                            offsetR,
                            terrain.textMapperSvgClass,
                            terrain.id
                        )
                    )
                }
            }
        }
        renderSb.appendLine("include https://campaignwiki.org/contrib/default.txt")
        return listOf(File(parentDirectory, "map.txt").apply { writeText(renderSb.toString()) })
    }
}
