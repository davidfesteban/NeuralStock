package dev.misei.einfachml.neuralservice.serial;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.misei.einfachml.neuralservice.domain.Connection;
import dev.misei.einfachml.neuralservice.domain.Layer;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.domain.Status;
import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkDeserializer extends JsonDeserializer<Network> {

    public Network deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        // Deserialize the fields
        Algorithm algorithm = jsonParser.getCodec().treeToValue(node.get("algorithm"), Algorithm.class);

        // Deserialize inboundFeeder and outboundFeeder as lists
        List<Connection> inboundFeeder = jsonParser.getCodec().readValue(
                node.get("inboundFeeder").traverse(jsonParser.getCodec()),
                new TypeReference<List<Connection>>() {
                });

        List<Connection> outboundFeeder = jsonParser.getCodec().readValue(
                node.get("outboundFeeder").traverse(jsonParser.getCodec()),
                new TypeReference<List<Connection>>() {
                });

        Status status = jsonParser.getCodec().treeToValue(node.get("status"), Status.class);

        // Deserialize the layers (ArrayList part)
        List<Layer> layers = new ArrayList<>();
        JsonNode layersNode = node.get("layers");
        if (layersNode != null) {
            for (JsonNode layerNode : layersNode) {
                Layer layer = jsonParser.getCodec().treeToValue(layerNode, Layer.class);
                layers.add(layer);
            }
        }

        // Create the Network object and set the values
        Network network = new Network();
        network.setAlgorithm(algorithm);
        network.addAll(layers);  // Add layers (ArrayList content)
        network.setInboundFeeder(inboundFeeder);  // Set inboundFeeder
        network.setOutboundFeeder(outboundFeeder);  // Set outboundFeeder
        network.setStatus(status);  // Set status

        // Optionally reconnect network if required by your business logic
        network.reconnectAll();

        return network;
    }
}
