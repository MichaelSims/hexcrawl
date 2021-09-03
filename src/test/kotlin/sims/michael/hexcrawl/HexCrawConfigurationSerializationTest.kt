package sims.michael.hexcrawl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals

class HexCrawConfigurationSerializationTest {

    private val logger: Logger = LoggerFactory.getLogger(HexCrawConfigurationSerializationTest::class.java)

    @Test
    fun testSerializationLoop() {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(TestConfiguration)
        logger.debug("Serialized to $serialized")
        val deserialized = mapper.readValue(serialized, HexCrawlConfiguration::class.java)
        assertEquals(TestConfiguration, deserialized)
    }
}
