package sims.michael.hexcrawl

fun Collection<IntRange>.mergeRanges(): IntRange {
    requireContiguousRanges(this)
    return reduce { acc, range -> acc.first..range.last }
}

fun requireContiguousRanges(ranges: Collection<IntRange>) {
    val sorted = ranges.sortedBy { it.first }
    for ((a, b) in sorted.windowed(2)) {
        require(a.last + 1 == b.first) {
            "Range $a is not contiguous with range $b"
        }
    }
}

val IntRange.size: Int get() = last - first + 1
