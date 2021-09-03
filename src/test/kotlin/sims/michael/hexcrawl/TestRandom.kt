package sims.michael.hexcrawl

class TestRandom(ints: Iterable<Int>) : Random {
    private val iterator = ints.iterator()
    override fun nextInt(bound: Int): Int = iterator.next()
}
