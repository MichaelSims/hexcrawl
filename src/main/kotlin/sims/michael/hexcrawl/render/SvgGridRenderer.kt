package sims.michael.hexcrawl.render

import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.util.XMLResourceDescriptor
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import sims.michael.hexcrawl.*
import java.io.InputStream
import java.io.StringWriter
import java.math.BigDecimal
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class SvgGridRenderer(private val config: HexCrawlConfiguration) : GridRenderer {

    private val logger = LoggerFactory.getLogger(SvgGridRenderer::class.java)

    override fun render(grid: MutableGrid): String =
        requireNotNull(SvgGridRenderer::class.java.getResourceAsStream("template.svg")).use { template ->
            renderWithTemplate(template, grid)
        }

    private fun renderWithTemplate(template: InputStream, grid: MutableGrid): String {
        val doc = SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName())
            .createDocument(null, template)

        val backgroundsElement = requireNotNull(doc.getElementById("backgrounds"))
        val coordinatesElement = requireNotNull(doc.getElementById("coordinates"))
        val regionsElement = requireNotNull(doc.getElementById("regions"))
        val labelsElement = requireNotNull(doc.getElementById("labels"))

        val qDelta = -grid.qRange.first
        val rDelta = -grid.rRange.first
        logger.debug("Translating grid by qDelta $qDelta rDelta $rDelta")
        val translated = grid.translate(qDelta, rDelta)

        doc.setViewBoxAttribute(grid)

        for (q in translated.qRange) {
            for (r in translated.rRange) {
                val coordinate = CubeCoordinate(q, r)
                val terrain = translated.get(coordinate)?.let { config.idToTerrainMap[it] }
                if (terrain != null) {
                    val (offsetQ, offsetR) = coordinate.toOffsetCoordinate()

                    val oddColumnOffset = if (offsetQ.isEven) ZERO else -HEX_SIDE_HEIGHT

                    // Add background (hex fill)
                    val backgroundElement = doc.createElement("use").apply {
                        setAttribute("x", (BigDecimal(offsetQ) * HEX_WIDTH_MINUS_ONE_SIDE).toString())
                        setAttribute("y", (BigDecimal(offsetR) * HEX_HEIGHT + oddColumnOffset).toString())
                        setAttribute("xlink:href", "#${terrain.textMapperSvgClass}")
                    }
                    backgroundsElement.appendChild(backgroundElement)

                    // Add region (hex stroke)
                    val regionElement = doc.createElement("polygon").apply {
                        setAttribute("fill", "none")
                        setAttribute("stroke", "black")
                        setAttribute("stroke-width", "3")
                        val xDelta = BigDecimal(offsetQ) * HEX_WIDTH_MINUS_ONE_SIDE
                        val yDelta = BigDecimal(offsetR) * HEX_HEIGHT + oddColumnOffset
                        setAttribute("points", getTranslatedHex(xDelta, yDelta).toString())
                    }
                    regionsElement.appendChild(regionElement)

                    // Add hex coordinate (i.e. "00.00", "00.01", "01.01", etc.)
                    val coordinateElement = doc.createElement("text").apply {
                        setAttribute("text-anchor", "middle")
                        setAttribute("font-size", "20pt")
                        setAttribute("dy", "15px")
                        setAttribute("x", (BigDecimal(offsetQ) * HEX_WIDTH_MINUS_ONE_SIDE).toString())
                        setAttribute(
                            "y",
                            (BigDecimal(offsetR) * HEX_HEIGHT + oddColumnOffset + HEX_COORDINATE_Y_OFFSET).toString()
                        )
                        appendChild(doc.createTextNode("%02d.%02d".format(offsetQ, offsetR)))
                    }
                    coordinatesElement.appendChild(coordinateElement)

                    // Add hex label (Terrain description, "Mountains", "Swamp", etc.)
                    val labelElement = doc.createElement("g").apply {
                        // TODO using CSS would probably be cleaner
                        fun createLabelElement() = doc.createElement("text").apply {
                            setAttribute("text-anchor", "middle")
                            setAttribute("font-size", "14pt")
                            setAttribute("dy", "5px")
                            setAttribute("x", (BigDecimal(offsetQ) * HEX_WIDTH_MINUS_ONE_SIDE).toString())
                            setAttribute(
                                "y",
                                (BigDecimal(offsetR) * HEX_HEIGHT + oddColumnOffset + HEX_LABEL_Y_OFFSET).toString()
                            )
                            appendChild(doc.createTextNode(terrain.id))
                        }

                        val highLightElement = createLabelElement().apply {
                            setAttribute("stroke", "white")
                            setAttribute("stroke-width", "5pt")
                        }
                        appendChild(highLightElement)
                        appendChild(createLabelElement())
                    }
                    labelsElement.appendChild(labelElement)
                }
            }
        }

        val stringWriter = StringWriter()
        TransformerFactory
            .newInstance()
            .newTransformer()
            .apply {
                setOutputProperty(OutputKeys.INDENT, "yes")
                setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
                setOutputProperty(OutputKeys.METHOD, "xml")
                setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            }
            .transform(DOMSource(doc), StreamResult(stringWriter))
        return stringWriter.toString()
    }

    private fun Document.setViewBoxAttribute(grid: MutableGrid) {
        // TODO too many magic values here, need to revisit
        val margin = BigDecimal("60")
        val maxX = BigDecimal(grid.qRange.size) * HEX_WIDTH_MINUS_ONE_SIDE + margin
        val maxY = BigDecimal(grid.rRange.size) * (HEX_HEIGHT + margin)
        val minX = -106
        val minY = 170
        documentElement.setAttribute("viewBox", "$minX $minY $maxX $maxY")
    }

    private val Int.isEven: Boolean get() = this % 2 == 0

    data class Point(val x: BigDecimal, val y: BigDecimal) {
        fun translate(xDelta: BigDecimal, yDelta: BigDecimal) = copy(x = x + xDelta, y = y + yDelta)
        override fun toString(): String = "$x,$y"
    }

    data class Hex(val a: Point, val b: Point, val c: Point, val d: Point, val e: Point, val f: Point) {
        fun translate(xDelta: BigDecimal, yDelta: BigDecimal) = copy(
            a = a.translate(xDelta, yDelta),
            b = b.translate(xDelta, yDelta),
            c = c.translate(xDelta, yDelta),
            d = d.translate(xDelta, yDelta),
            e = e.translate(xDelta, yDelta),
            f = f.translate(xDelta, yDelta),
        )

        override fun toString(): String = listOf(a, b, c, d, e, f).joinToString(" ")
    }

    private fun getTranslatedHex(xDelta: BigDecimal, yDelta: BigDecimal): Hex {
        val protoHex = Hex(
            Point(BigDecimal("-100.0"), BigDecimal("0.0")),
            Point(BigDecimal("-50.0"), BigDecimal("86.6")),
            Point(BigDecimal("50.0"), BigDecimal("86.6")),
            Point(BigDecimal("100.0"), BigDecimal("0.0")),
            Point(BigDecimal("50.0"), BigDecimal("-86.6")),
            Point(BigDecimal("-50.0"), BigDecimal("-86.6")),
        )
        return protoHex.translate(xDelta, yDelta)
    }

    companion object {
        private val ZERO = BigDecimal("0.0")
        private val HEX_HEIGHT = BigDecimal("173.2")
        private val HEX_SIDE_HEIGHT = BigDecimal("86.6")
        private val HEX_WIDTH_MINUS_ONE_SIDE = BigDecimal("150.0")
        private val HEX_COORDINATE_Y_OFFSET = -BigDecimal("69.3")
        private val HEX_LABEL_Y_OFFSET = BigDecimal("69.3")
    }
}
