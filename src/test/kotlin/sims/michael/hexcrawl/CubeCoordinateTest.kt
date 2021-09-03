package sims.michael.hexcrawl

import org.junit.jupiter.api.Test
import sims.michael.hexcrawl.FillDirection.*
import kotlin.test.assertEquals

class CubeCoordinateTest {
    @Test
    fun testGetAdjacentHexesNorthClockwise() {
        assertEquals(
            listOf(
                CubeCoordinate(0, -1),
                CubeCoordinate(1, -1),
                CubeCoordinate(1, 0),
                CubeCoordinate(0, 1),
                CubeCoordinate(-1, 1),
                CubeCoordinate(-1, 0)
            ),
            CubeCoordinate.ZERO.getAdjacentPoints(NorthClockwise)
        )
    }

    @Test
    fun testGetAdjacentHexesNorthCounterclockwise() {
        assertEquals(
            listOf(
                CubeCoordinate(0, -1),
                CubeCoordinate(-1, 0),
                CubeCoordinate(-1, 1),
                CubeCoordinate(0, 1),
                CubeCoordinate(1, 0),
                CubeCoordinate(1, -1)
            ),
            CubeCoordinate.ZERO.getAdjacentPoints(NorthCounterclockwise)
        )
    }

    @Test
    fun testGetAdjacentHexesSouthClockwise() {
        assertEquals(
            listOf(
                CubeCoordinate(0, 1),
                CubeCoordinate(-1, 1),
                CubeCoordinate(-1, 0),
                CubeCoordinate(0, -1),
                CubeCoordinate(1, -1),
                CubeCoordinate(1, 0)
            ),
            CubeCoordinate.ZERO.getAdjacentPoints(SouthClockwise)
        )
    }

    @Test
    fun testGetAdjacentHexesSouthCounterclockwise() {
        assertEquals(
            listOf(
                CubeCoordinate(0, 1),
                CubeCoordinate(1, 0),
                CubeCoordinate(1, -1),
                CubeCoordinate(0, -1),
                CubeCoordinate(-1, 0),
                CubeCoordinate(-1, 1)
            ),
            CubeCoordinate.ZERO.getAdjacentPoints(SouthCounterclockwise)
        )
    }
}
