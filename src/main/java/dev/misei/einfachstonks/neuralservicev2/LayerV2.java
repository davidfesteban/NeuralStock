package dev.misei.einfachstonks.neuralservicev2;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LayerV2 {

    List<NeuronV2> neurons;

    BigDecimal totalLoss;

    public void computeForward() {
        neurons.forEach(NeuronV2::computeForward);
    }

    public void computeBackward(){

    }

    public void computeTotalLoss(List<BigDecimal> expected) {
        List<BigDecimal> outputs = outputNeurons.stream().map(NeuronV2::getActivation).toList();

        totalLoss = new BiFunction<List<BigDecimal>, List<BigDecimal>, BigDecimal>() {
            @Override
            public BigDecimal apply(List<BigDecimal> outputs, List<BigDecimal> expectedValues) {
                BigDecimal temporalLoss = new BigDecimal(0);


                for (int i = 0; i < outputs.size(); i++) {
                    var output = outputs.get(i);
                    var expected = expectedValues.get(i);

                    var derivativeLoss = output.subtract(expected);

                    temporalLoss = temporalLoss.add(derivativeLoss.pow(2));
                }

                return temporalLoss.divide(new BigDecimal(outputs.size()));
            }
        }.apply(outputs, expected);


    }
}
