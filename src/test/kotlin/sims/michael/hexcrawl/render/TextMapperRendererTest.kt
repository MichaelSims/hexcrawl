package sims.michael.hexcrawl.render

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import sims.michael.hexcrawl.CubeCoordinate
import sims.michael.hexcrawl.MutableGrid
import sims.michael.hexcrawl.TestConfiguration
import sims.michael.hexcrawl.getSpiralPath

class TextMapperRendererTest {

    private val testGrid = MutableGrid().apply {
        CubeCoordinate.ZERO.getSpiralPath().take(30).forEachIndexed { i, cubeCoordinate ->
            set(cubeCoordinate, TestConfiguration.terrainList[i % TestConfiguration.terrainList.size].id)
        }
    }

    @Test
    fun testRender() {
        println(GridRenderer.getTextMapperRenderer(TestConfiguration).render(testGrid))
    }
}
