package dev.misei.einfachstonks;

import dev.misei.einfachstonks.neuralservice.dataset.DataSet;
import dev.misei.einfachstonks.neuralservice.dataset.DataSetList;
import dev.misei.einfachstonks.neuralservice.math.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasureType;
import dev.misei.einfachstonks.neuralservice.math.MathStonk;
import dev.misei.einfachstonks.neuralservice.network.Context;
import dev.misei.einfachstonks.neuralservice.network.NeuralNetworkService;
import dev.misei.einfachstonks.stonkservice.StonkService;
import dev.misei.einfachstonks.stonkservice.dto.ETFDetailDTO;
import dev.misei.einfachstonks.stonkservice.model.ETFBridgeType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    void getGoldPrice_AndPredict() throws ExecutionException, InterruptedException {
        var internalUUID = stonkService.createETFTracker("Invesco Physical Gold", ETFBridgeType.YAHOO, "SGLD.L", ETFType.INDIVIDUAL_POSITIVE);
        stonkService.track(internalUUID, true);

        DataSetList dataSetList = new DataSetList();
        var multimap = stonkService.returnAll();
        multimap.forEach(new BiConsumer<ETFType, List<ETFDetailDTO>>() {
            @Override
            public void accept(ETFType etfType, List<ETFDetailDTO> etfDetailDTOS) {
                etfDetailDTOS.forEach(new Consumer<ETFDetailDTO>() {
                    @Override
                    public void accept(ETFDetailDTO etfDetailDTO) {
                        if(etfDetailDTO.composite().canBeUsed()) {
                            List<Double> output = List.of(etfDetailDTO.composite().resultsAsList().getFirst());
                            List<Double> inputs = new ArrayList<>(etfDetailDTO.history().asList());
                            inputs.addAll(etfDetailDTO.composite().asList());

                            dataSetList.accumulateTraining(new DataSet(inputs, output));
                        } else {
                            System.out.println("Not used!");
                        }
                    }
                });
            }
        });


        var learningRatio = 0.01;
        var momentum = 0.9;
        var neuronsPerHiddenLayer = 15;
        int totalHiddenLayers = 3;

        var uuid = neuralNetworkService.createNetwork(dataSetList, new Context(learningRatio, momentum, AlgorithmType.SIGMOID, ErrorMeasureType.LINEAR),
                neuronsPerHiddenLayer, totalHiddenLayers);
        neuralNetworkService.trainAll(50);


        var multimap2 = stonkService.singleSnapshot(LocalDate.of(2024, 5, 26));
        multimap2.forEach(new BiConsumer<ETFType, List<ETFDetailDTO>>() {
            @Override
            public void accept(ETFType etfType, List<ETFDetailDTO> etfDetailDTOS) {
                etfDetailDTOS.forEach(new Consumer<ETFDetailDTO>() {
                    @Override
                    public void accept(ETFDetailDTO etfDetailDTO) {
                        List<Double> inputs = new ArrayList<>(etfDetailDTO.history().asList());
                        inputs.addAll(etfDetailDTO.composite().asList());

                        try {
                            System.out.println(neuralNetworkService.predict(uuid, new DataSet(inputs, List.of()), false));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });


        //neuralNetworkService.createNetworks(null);
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
    }
}