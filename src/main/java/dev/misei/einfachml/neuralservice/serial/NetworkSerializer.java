package dev.misei.einfachml.neuralservice.serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.misei.einfachml.neuralservice.domain.Layer;
import dev.misei.einfachml.neuralservice.domain.Network;

import java.io.IOException;

public class NetworkSerializer extends JsonSerializer<Network> {

    @Override
    public void serialize(Network network, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();  // Start the network object

        gen.writeObjectField("algorithm", network.getAlgorithm());
        gen.writeObjectField("inboundFeeder", network.getInboundFeeder());
        gen.writeObjectField("outboundFeeder", network.getOutboundFeeder());
        gen.writeObjectField("status", network.getStatus());

        gen.writeArrayFieldStart("layers");
        for (Layer layer : network) {
            gen.writeObject(layer);
        }
        gen.writeEndArray();  // End layers array

        gen.writeEndObject();  // End network object
    }
}
