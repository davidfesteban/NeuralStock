package dev.misei.einfachml.neuralservice.serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.misei.einfachml.neuralservice.domain.shape.Shape;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;

import java.io.IOException;

public class ShapeSerializer extends JsonSerializer<Shape> {

    @Override
    public void serialize(Shape shape, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        if (shape instanceof StandardShape) {
            gen.writeStringField("name", shape.getName());
        }
        gen.writeEndObject();
    }
}
