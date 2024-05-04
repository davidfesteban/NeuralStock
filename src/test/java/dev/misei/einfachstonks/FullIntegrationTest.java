package dev.misei.einfachstonks;

import dev.misei.einfachstonks.neuralservice.NeuralNetworkService;
import dev.misei.einfachstonks.stonkservice.StonkService;
import dev.misei.einfachstonks.stonkservice.model.ETFType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

@Testcontainers
@SpringBootTest
public class FullIntegrationTest {

    @Container
    static MongoDBContainer container = new MongoDBContainer(DockerImageName.parse("mongo:5.0.26"));

    @Autowired
    StonkService stonkService;

    @Autowired
    NeuralNetworkService neuralNetworkService;

    @Test
    void getGoldPrice_AndPredict() {
        var internalUUID = stonkService.createETFTracker("Invesco Physical Gold", null, null, "SGLD.L", ETFType.INDIVIDUAL_POSITIVE);
        stonkService.trackETF(internalUUID, LocalDate.now().minusWeeks(60));
        var multimap = stonkService.snapshot(LocalDate.now().minusWeeks(60), LocalDate.now());
        neuralNetworkService.createNetworks(null);
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
    }
}