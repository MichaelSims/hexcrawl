package sims.michael.hexcrawl

import java.util.Random as JavaUtilRandom
import java.security.SecureRandom

interface Random {
    fun nextInt(bound: Int): Int

    companion object {
        fun javaUtilRandom(seed: Long? = null) =
            JavaUtilRandomAdapter(if (seed == null) JavaUtilRandom() else JavaUtilRandom(seed))

        fun secureRandom() = SecureRandomAdapter(SecureRandom())
    }
}

class JavaUtilRandomAdapter(private val javaUtilRandom: JavaUtilRandom) : Random {
    override fun nextInt(bound: Int): Int = javaUtilRandom.nextInt(bound)
}

class SecureRandomAdapter(private val secureRandom: SecureRandom) : Random {
    override fun nextInt(bound: Int): Int = secureRandom.nextInt(bound)
}
