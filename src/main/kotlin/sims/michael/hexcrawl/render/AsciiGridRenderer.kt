package sims.michael.hexcrawl.render

import dk.ilios.asciihexgrid.AsciiBoard
import dk.ilios.asciihexgrid.printers.LargeFlatAsciiHexPrinter
import sims.michael.hexcrawl.CubeCoordinate
import sims.michael.hexcrawl.MutableGrid

class AsciiGridRenderer : GridRenderer {

    override fun render(grid: MutableGrid): String {
        // AsciiBoard can't handle negative columns or rows, so we need to translate the grid first
        val translated = grid.translate(-grid.qRange.first, -grid.rRange.first)
        return renderAsAsciiBoard(translated)
    }

    private fun renderAsAsciiBoard(grid: MutableGrid): String {
        val board = AsciiBoard(
            grid.qRange.first,
            grid.qRange.last,
            grid.rRange.first,
            grid.rRange.last,
            LargeFlatAsciiHexPrinter()
        )

        for (q in grid.qRange) {
            for (r in grid.rRange) {
                val label = grid.get(CubeCoordinate(q, r))
                if (label != null) {
                    board.printHex(label, "", ' ', q, r)
                }
            }
        }

        return board.prettyPrint(true)
    }
}
