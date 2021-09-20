package sims.michael.hexcrawl.render

import dk.ilios.asciihexgrid.AsciiBoard
import dk.ilios.asciihexgrid.printers.LargeFlatAsciiHexPrinter
import sims.michael.hexcrawl.CubeCoordinate
import sims.michael.hexcrawl.MutableGrid
import java.io.File

class AsciiGridRenderer : GridRenderer {

    override fun renderToFile(grid: MutableGrid, parentDirectory: File, fileNumber: String): List<File> {
        // AsciiBoard can't handle negative columns or rows, so we need to translate the grid first
        val translated = grid.translate(-grid.qRange.first, -grid.rRange.first)
        return renderAsAsciiBoard(translated, parentDirectory, fileNumber)
    }

    private fun renderAsAsciiBoard(grid: MutableGrid, parentDirectory: File, fileNumber: String): List<File> {
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

        val outputFile = File(parentDirectory, "map$fileNumber.txt")
        outputFile.writeText(board.prettyPrint(true))
        return listOf(outputFile)
    }
}
