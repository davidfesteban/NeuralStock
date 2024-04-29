package dev.misei.einfachstonks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
class StonksV2ApplicationTests {

    private Network network;

    @Test
    void givenDatasetDouble_whenSum_thenProbabilitySum() {
        network = new Network(generateRandomDataByBatch(10000), 2, 1);
        network.train(1);

        network.predictComplete(new DataSet(List.of(0.3, 0.1, 0.1), new ArrayList<>()));
    }

    private DataSet threeRandomDoubleNumber() {
        var random = new Random();

        List<Double> testData = List.of(random.nextDouble(1), random.nextDouble(1), random.nextDouble(1));
        List<Double> resultData = List.of(testData.stream().reduce(Double::sum).get());

        return new DataSet(testData, resultData);
    }

    private DataSetList generateRandomDataByBatch(int amount) {
        DataSetList data = new DataSetList();

        for (int i = 0; i < amount; i++) {
            data.accumulateTraining(threeRandomDoubleNumber());
        }

        return data;
    }

}
