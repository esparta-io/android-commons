package io.esparta.library.gsonconverter

import com.google.gson.*
import org.joda.time.LocalDate
import java.lang.reflect.Type

class LocalDateTypeConverter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    override fun serialize(src: LocalDate, srcType: Type, context: JsonSerializationContext): JsonElement = JsonPrimitive(src.toString())

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): LocalDate = LocalDate(json.asString)
}