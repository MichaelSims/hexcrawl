package sims.michael.hexcrawl.render

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sims.michael.hexcrawl.*
import java.nio.file.Files
import kotlin.io.path.absolutePathString

class SvgGridRendererTest {

    private val logger: Logger = LoggerFactory.getLogger(SvgGridRendererTest::class.java)

    private val testGridSpiral = MutableGrid().apply {
        CubeCoordinate.ZERO.getSpiralPath().take(30).forEachIndexed { i, cubeCoordinate ->
            set(cubeCoordinate, TestConfiguration.terrainList[i % TestConfiguration.terrainList.size].id)
        }
    }

    private val testGridSquare = MutableGrid().apply {
        val numRows = 5
        val numColumns = 5
        var counter = 0
        for (row in 0 until numRows) {
            for (column in 0 until numColumns) {
                set(
                    OffsetCoordinate(column, row).toCubeCoordinate(),
                    TestConfiguration.terrainList[counter++ % TestConfiguration.terrainList.size].id
                )
            }
        }
    }

    @Test
    fun testSpiralRender() = renderAndOpen(testGridSpiral)

    @Test
    fun testSquareRender() = renderAndOpen(testGridSquare)

    private fun renderAndOpen(grid: MutableGrid) {
        val tempDir = Files.createTempDirectory(SvgGridRendererTest::class.simpleName).toFile()
        val files = SvgGridRenderer(TestConfiguration).renderToFile(grid, tempDir, "000")
        logger.debug("$files")
        for (file in files) {
            ProcessBuilder()
                .command("open", file.absolutePath)
                .start()
        }
    }
}
