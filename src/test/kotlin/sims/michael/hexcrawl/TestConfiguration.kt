package sims.michael.hexcrawl

val TestConfiguration = HexCrawlConfiguration(
    terrainList = listOf(
        Terrain(
            "Plains",
            "plain",
            mapOf(
                1..14 to "Plains",
                15..16 to "Water",
                17..18 to "Forest",
                19..19 to "Hills",
                20..20 to "Desert"
            )
        ),
        Terrain(
            "Hills",
            "hill",
            mapOf(
                1..14 to "Hills",
                15..16 to "Forest",
                17..18 to "Mountains",
                19..19 to "Plains",
                20..20 to "Barrens"
            )
        ),
        Terrain(
            "Forest",
            "forest",
            mapOf(
                1..14 to "Forest",
                15..17 to "Mountains",
                18..19 to "Hills",
                20..20 to "Plains"
            )
        ),
        Terrain(
            "Water",
            "sea",
            mapOf(
                1..16 to "Water",
                17..19 to "Swamp",
                20..20 to "Plains"
            )
        ),
        Terrain(
            "Mountains",
            "mountain",
            mapOf(
                1..14 to "Mountains",
                15..16 to "Tundra",
                17..18 to "Barrens",
                19..19 to "Hills",
                20..20 to "Forest"
            )
        ),
        Terrain(
            "Swamp",
            "swamp",
            mapOf(
                1..15 to "Swamp",
                16..19 to "Water",
                20..20 to "Plains"
            )
        ),
        Terrain(
            "Barrens",
            "coast",
            mapOf(
                1..14 to "Barrens",
                15..17 to "Mountains",
                18..18 to "Hills",
                19..19 to "Desert",
                20..20 to "Tundra"
            )
        ),
        Terrain(
            "Desert",
            "sand",
            mapOf(
                1..16 to "Desert",
                17..18 to "Plains",
                19..19 to "Hills",
                20..20 to "Barrens"
            )
        ),
        Terrain(
            "Tundra",
            "tundra",
            mapOf(
                1..15 to "Tundra",
                16..18 to "Mountains",
                19..19 to "Forest",
                20..20 to "Barrens"
            )
        )
    ),
    fillDirectionDieResultMap = mapOf(
        1..2 to FillDirection.NorthClockwise,
        3..4 to FillDirection.NorthCounterclockwise,
        5..6 to FillDirection.SouthClockwise,
        7..8 to FillDirection.SouthCounterclockwise
    ),
    rarityDieResultMapTwoInputs = mapOf(
        1..4 to Rarity.Uncommon,
        5..6 to Rarity.Common
    ),
    rarityDieResultMapThreeInputs = mapOf(
        1..3 to Rarity.Uncommon,
        4..5 to Rarity.Middle,
        6..6 to Rarity.Common
    )
)
