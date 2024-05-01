package dev.misei.einfachstonks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
class StonksV2ApplicationTests {

    private Network network;

    @Test
    void givenDatasetDouble_whenSum_thenProbabilitySum() {
        network = new Network(generateRandomDataByBatch(100), 10, 2);
        network.train(10000);

        network.predictComplete(new DataSet(List.of(0.0, 0.0), new ArrayList<>()));
        network.predictComplete(new DataSet(List.of(0.5, 0.3), new ArrayList<>()));
    }

    @Test
    void givenDatasetLogic_whenAND_thenANDBool() {
        network = new Network(generateAndAnd_And_Door(), 10, 2);
        network.train(10000);

        network.predictComplete(new DataSet(List.of(0.0, 0.0, 0.0), new ArrayList<>()));
        network.predictComplete(new DataSet(List.of(0.0, 1.0, 0.0), new ArrayList<>()));
        network.predictComplete(new DataSet(List.of(1.0, 1.0, 1.0), new ArrayList<>()));
    }

    private DataSetList generateAndAnd_And_Door() {
        DataSetList dataSetList = new DataSetList();

        dataSetList.accumulateTraining(new DataSet(List.of(0.0,0.0,0.0), List.of(0.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(0.0,0.0,1.0), List.of(0.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(0.0,1.0,0.0), List.of(0.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(0.0,1.0,1.0), List.of(0.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(1.0,0.0,0.0), List.of(0.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(1.0,0.0,1.0), List.of(0.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(1.0,1.0,0.0), List.of(0.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(1.0,1.0,1.0), List.of(1.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(1.0,1.0,1.0), List.of(1.0)));
        dataSetList.accumulateTraining(new DataSet(List.of(1.0,1.0,1.0), List.of(1.0)));

        return dataSetList;
    }

    private DataSet threeRandomDoubleNumber() {
        var random = new Random();

        List<Double> testData = List.of(Math.floor(random.nextDouble(1)*10)/10, Math.floor(random.nextDouble(1)*10)/10);
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
