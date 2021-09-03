package sims.michael.hexcrawl.render

import org.junit.jupiter.api.Test
import sims.michael.hexcrawl.CubeCoordinate
import sims.michael.hexcrawl.MutableGrid
import sims.michael.hexcrawl.TestConfiguration
import sims.michael.hexcrawl.getSpiralPath

class AsciiGridRendererTest {

    private val testGrid = MutableGrid().apply {
        CubeCoordinate.ZERO.getSpiralPath().take(30).forEachIndexed { i, cubeCoordinate ->
            set(cubeCoordinate, TestConfiguration.terrainList[i % TestConfiguration.terrainList.size].let { terrain ->
                "${terrain.textMapperSvgClass} \"${terrain.id}\""
            })
        }
    }

    @Test
    fun testRender() {
        println(GridRenderer.getAsciiRenderer().render(testGrid))
    }
}
