package sims.michael.hexcrawl

class MutableGrid {
    private val grid = mutableMapOf<CubeCoordinate, String>()

    fun get(coordinate: CubeCoordinate): String? = grid[coordinate]
    fun set(coordinate: CubeCoordinate, label: String) {
        grid[coordinate] = label
    }

    val qRange: IntRange get() = rangeOf(CubeCoordinate::q)
    val rRange: IntRange get() = rangeOf(CubeCoordinate::r)

    private fun rangeOf(property: CubeCoordinate.() -> Int): IntRange =
        grid.keys.minOf(property)..grid.keys.maxOf(property)

    fun translate(qDelta: Int, rDelta: Int): MutableGrid {
        val outer = this
        return MutableGrid().apply {
            for ((coordinate, label) in outer.grid) {
                set(coordinate.translate(qDelta, rDelta), label)
            }
        }
    }
}
