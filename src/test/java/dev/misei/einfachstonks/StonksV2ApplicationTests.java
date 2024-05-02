package dev.misei.einfachstonks;

import dev.misei.einfachstonks.dataset.DataSet;
import dev.misei.einfachstonks.dataset.DataSetList;
import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.network.Context;
import dev.misei.einfachstonks.network.Network;
import dev.misei.einfachstonks.network.NetworkFactory;
import lombok.SneakyThrows;
import org.apache.tomcat.util.threads.TaskThread;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
class StonksV2ApplicationTests {



    @Test
    void givenDatasetDouble_whenSum_thenProbabilitySum() throws InterruptedException, ExecutionException {
        // Create an ExecutorService using virtual threads
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();


        var future = executor.submit(new Callable<Network>() {
            @Override
            public Network call() throws Exception {
                Network network = NetworkFactory.create(generateRandomDataByBatch(100), new Context(0.01, 0.9),30, 1, Algorithm.SIGMOID, ErrorMeasure.LINEAR);;
                network.train(10000);
                return network;
            }
        });
        var future2 = executor.submit(new Callable<Network>() {
            @Override
            public Network call() throws Exception {
                Network network = NetworkFactory.create(generateRandomDataByBatch(100), new Context(0.01, 0.9),10, 2, Algorithm.SIGMOID, ErrorMeasure.LINEAR);;
                network.train(10000);
                return network;
            }
        });

        var future3 = executor.submit(new Callable<Network>() {
            @Override
            public Network call() throws Exception {
                Network network = NetworkFactory.create(generateRandomDataByBatch(100), new Context(0.01, 0.9),20, 1, Algorithm.LEAKY_RELU, ErrorMeasure.LINEAR);
                network.train(10000);
                return network;
            }
        });

        var network1 = future.get();
        var network2 = future2.get();
        var network3 = future3.get();

        System.out.println(network1.predict(new DataSet(List.of(0.0, 0.0), new ArrayList<>())));
        System.out.println(network1.predict(new DataSet(List.of(0.5, 0.3), new ArrayList<>())));

        System.out.println(network2.predict(new DataSet(List.of(0.0, 0.0), new ArrayList<>())));
        System.out.println(network2.predict(new DataSet(List.of(0.5, 0.3), new ArrayList<>())));

        System.out.println(network3.predict(new DataSet(List.of(0.0, 0.0), new ArrayList<>())));
        System.out.println(network3.predict(new DataSet(List.of(0.5, 0.3), new ArrayList<>())));


        executor.shutdownNow();
        //executor.awaitTermination(1, TimeUnit.HOURS);

    }

    @Test
    void givenDatasetLogic_whenAND_thenANDBool() {
        //var networkFactory = new NetworkFactory(generateAndAnd_And_Door(), new Context(0.01, 0.9), Algorithm.SIGMOID, ErrorMeasure.LINEAR);
        //network = networkFactory.create(20, 1);
        //network.train(10000);
//
        //network.predict(new DataSet(List.of(0.0, 0.0, 0.0), new ArrayList<>()));
        //network.predict(new DataSet(List.of(0.0, 1.0, 0.0), new ArrayList<>()));
        //network.predict(new DataSet(List.of(1.0, 1.0, 1.0), new ArrayList<>()));
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
