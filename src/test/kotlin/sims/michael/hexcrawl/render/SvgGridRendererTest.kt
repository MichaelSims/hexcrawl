package sims.michael.hexcrawl.render

import org.junit.jupiter.api.Test
import sims.michael.hexcrawl.*
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.io.path.writeText

class SvgGridRendererTest {

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
        val output = SvgGridRenderer(TestConfiguration).render(grid)
        val tempFile = Files.createTempFile(SvgGridRendererTest::class.simpleName, ".svg")
        tempFile.writeText(output)
        ProcessBuilder()
            .command("open", tempFile.absolutePathString())
            .start()
    }
}
