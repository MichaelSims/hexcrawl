package sims.michael.hexcrawl

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class IntRangesTest {

    @Test
    fun testRequireContiguousRanges() {
        requireContiguousRanges(listOf(1..2, 3..4))
        assertFails {
            requireContiguousRanges(listOf(1..1, 3..4))
        }
        assertFails {
            requireContiguousRanges(listOf(1..3, 3..4))
        }
    }

    @Test
    fun testMergeRanges() {
        assertEquals(1..17, listOf(1..15, 16..17).mergeRanges())
    }

    @Test
    fun testIntRangeSize() {
        assertEquals(20, listOf(1..15, 16..16, 17..20).mergeRanges().size)
    }
}
