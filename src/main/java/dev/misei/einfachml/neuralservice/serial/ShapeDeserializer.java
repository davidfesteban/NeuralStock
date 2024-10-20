package dev.misei.einfachml.neuralservice.serial;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.misei.einfachml.neuralservice.domain.shape.Shape;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;

import java.io.IOException;

public class ShapeDeserializer extends JsonDeserializer<Shape> {

    @Override
    public Shape deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name = node.get("name").asText();

        return StandardShape.valueOf(name);
    }
}
