package sims.michael.hexcrawl.render

import sims.michael.hexcrawl.HexCrawlConfiguration
import sims.michael.hexcrawl.MutableGrid

interface GridRenderer {
    fun render(grid: MutableGrid): String

    enum class RendererId(val optionName: String) {
        Ascii("ascii"), TextMapper("text-mapper"), Svg("svg")
    }

    companion object {
        fun getRendererById(rendererId: RendererId, config: HexCrawlConfiguration): GridRenderer = when (rendererId) {
            RendererId.Ascii -> getAsciiRenderer()
            RendererId.TextMapper -> getTextMapperRenderer(config)
            RendererId.Svg -> getSvgRenderer(config)
        }

        fun getAsciiRenderer() = AsciiGridRenderer()
        fun getTextMapperRenderer(config: HexCrawlConfiguration) = TextMapperRenderer(config)
        fun getSvgRenderer(config: HexCrawlConfiguration) = SvgGridRenderer(config)

    }
}
