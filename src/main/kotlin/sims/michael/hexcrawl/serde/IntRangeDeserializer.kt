package sims.michael.hexcrawl.serde

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer

class IntRangeDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, ctxt: DeserializationContext): Any {
        val (min, max) = key.split("\\.\\.".toRegex()).map(String::toInt).take(2)
        return min..max
    }
}
