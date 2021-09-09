package sims.michael.hexcrawl

import sims.michael.hexcrawl.FillDirection.NorthClockwise
import sims.michael.hexcrawl.HexSide.*

/**
 * Represents a hex in a hex grid using a "cube coordinate", identified by a [q], [r], and [s] triple.
 * `q` is "columns" and an alias for `x`.
 * `r` is "rows" and an alias for `z`.
 * `s` is an alias for `y` and is derived from `q` and `r` so that `q + r + s = 0`.
 *
 * See [Hexagonal Grids from Red Blog Games](https://www.redblobgames.com/grids/hexagons/) and the
 * [implementation page](https://www.redblobgames.com/grids/hexagons/implementation.html) for more info. Note that
 * `q`, `r`, and `s` have different orders on the main page vs the implementation page. This implementation uses the
 * order of the **main** page (q -> x, s -> y, r -> z).
 *
 * ```
 *              _ _ _ _
 *             /       \
 *            /         \
 *    _ _ _ _/  0,-1,1   \_ _ _ _
 *   /       \           /       \
 *  /         \         /         \
 * /  -1,0,1   \_ _ _ _/  1,-1,0   \
 * \           /       \           /
 *  \         /         \         /
 *   \_ _ _ _/   0,0,0   \_ _ _ _/
 *   /       \           /       \
 *  /         \         /         \
 * /  -1,1,0   \_ _ _ _/  1,0,-1   \
 * \           /       \           /
 *  \         /         \         /
 *   \_ _ _ _/  0,1,-1   \_ _ _ _/
 *           \           /
 *            \         /
 *             \_ _ _ _/
 * ```
 */
data class CubeCoordinate(val q: Int, val r: Int, val s: Int = -q - r) {

    init {
        val sum = q + r + s
        require(sum == 0) { "q + r + s != 0 ($sum)" }
    }

    // TODO consider ditching the "point" language globally
    fun getAdjacentPoints(fillDirection: FillDirection = NorthClockwise): List<CubeCoordinate> =
        HexSide.orderedByFillDirection(fillDirection).map { side -> getAdjacentPoint(side) }

    private fun getAdjacentPoint(hexSide: HexSide): CubeCoordinate = when (hexSide) {
        N -> CubeCoordinate(q, r - 1)
        NE -> CubeCoordinate(q + 1, r - 1)
        SE -> CubeCoordinate(q + 1, r)
        S -> CubeCoordinate(q, r + 1)
        SW -> CubeCoordinate(q - 1, r + 1)
        NW -> CubeCoordinate(q - 1, r)
    }

    fun translate(qDelta: Int, rDelta: Int, sDelta: Int = -qDelta - rDelta): CubeCoordinate =
        CubeCoordinate(q + qDelta, r + rDelta, s + sDelta)

    override fun toString(): String = "($q, $s, $r)"

    companion object {
        val ZERO = CubeCoordinate(0, 0, 0)
    }
}

fun CubeCoordinate.toOffsetCoordinate() = OffsetCoordinate(
    q = q,
    r = r + (q + (q and 1)) / 2
)

/** Returns a sequence of [CubeCoordinate]s that form an endless clockwise spiral from [this]. */
fun CubeCoordinate.getSpiralPath(): Sequence<CubeCoordinate> = sequence {
    var q = q
    var r = r
    yield(this@getSpiralPath)
    var ring = 1
    while (true) {
        repeat(ring) { yield(CubeCoordinate(++q, r)) }
        repeat(ring - 1) { yield(CubeCoordinate(q, ++r)) }
        repeat(ring) { yield(CubeCoordinate(--q, ++r)) }
        repeat(ring) { yield(CubeCoordinate(--q, r)) }
        repeat(ring) { yield(CubeCoordinate(q, --r)) }
        repeat(ring) { yield(CubeCoordinate(++q, --r)) }
        ring++
    }
}
