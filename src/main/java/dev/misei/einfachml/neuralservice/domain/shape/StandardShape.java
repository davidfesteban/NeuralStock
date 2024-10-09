package dev.misei.einfachml.neuralservice.domain.shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum StandardShape implements Shape {
    PERCEPTRON {
        @Override
        public List<List<Integer>> draw(int inputSize, int outputSize, double complexity, boolean tridimensional) {
            List<List<Integer>> shape = new ArrayList<>();
            shape.add(List.of(inputSize));

            int hiddenLayers = (int) Math.round((1 + Math.log10(inputSize)) * complexity);
            int neuronsPerLayer = (int) Math.round(inputSize * 1.20 * complexity);

            for (int i = 0; i < hiddenLayers; i++) {
                shape.add(createLayer(neuronsPerLayer, tridimensional));
            }

            shape.add(List.of(outputSize));

            return shape;
        }
    },
    SQUARE {
        @Override
        public List<List<Integer>> draw(int inputSize, int outputSize, double complexity, boolean tridimensional) {
            List<List<Integer>> shape = new ArrayList<>();
            shape.add(List.of(inputSize));

            int hiddenLayers = (int) Math.round((1 + Math.log10(inputSize)) * complexity);
            int neuronsPerLayer = (int) Math.round(inputSize * outputSize * complexity);

            for (int i = 0; i < hiddenLayers; i++) {
                shape.add(createLayer(neuronsPerLayer, tridimensional));
            }

            shape.add(List.of(outputSize));

            return shape;
        }
    },
    TRIANGLE {
        @Override
        public List<List<Integer>> draw(int inputSize, int outputSize, double complexity, boolean tridimensional) {
            List<List<Integer>> shape = new ArrayList<>();
            shape.add(List.of(inputSize));

            int neuronsInCurrentLayer = (int) Math.round(inputSize * complexity);

            while (neuronsInCurrentLayer > outputSize) {
                shape.add(createLayer(neuronsInCurrentLayer, tridimensional));
               --neuronsInCurrentLayer;
            }

            shape.add(List.of(outputSize));

            return shape;
        }
    },
    COMPRESSOR {
        @Override
        public List<List<Integer>> draw(int inputSize, int outputSize, double complexity, boolean tridimensional) {
            List<List<Integer>> shape = new ArrayList<>();
            shape.add(List.of(inputSize));

            int neuronsInCurrentLayer = (int) Math.round(inputSize * complexity);

            int hiddenLayers = (int) (Math.round(Math.round(1 + Math.log10(inputSize)) * 3 * complexity));

            // First half: Compression phase (reduce by 30%)
            for (int i = 0; i < hiddenLayers / 2; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 0.6);
                shape.add(createLayer(neuronsInCurrentLayer, tridimensional));
            }


            for (int i = hiddenLayers / 2; i < hiddenLayers; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 1.4);
                shape.add(createLayer(neuronsInCurrentLayer, tridimensional));
            }

            shape.add(List.of(outputSize));

            return shape;
        }

    },
    EXPANDER {
        @Override
        public List<List<Integer>> draw(int inputSize, int outputSize, double complexity, boolean tridimensional) {
            List<List<Integer>> shape = new ArrayList<>();
            shape.add(List.of(inputSize));

            int neuronsInCurrentLayer = (int) Math.round(inputSize * complexity);

            int hiddenLayers = (int) (Math.round(Math.round(1 + Math.log10(inputSize)) * 3 * complexity));

            for (int i = 0; i < hiddenLayers / 2; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 1.4);
                shape.add(createLayer(neuronsInCurrentLayer, tridimensional));
            }


            for (int i = hiddenLayers / 2; i < hiddenLayers; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 0.6);
                shape.add(createLayer(neuronsInCurrentLayer, tridimensional));
            }

            shape.add(List.of(outputSize));

            return shape;
        }
    };

    private static List<Integer> createLayer(int neuronsInLayer, boolean tridimensional) {
        if (tridimensional) {
            return new ArrayList<>(Collections.nCopies(neuronsInLayer, neuronsInLayer));
        } else {
            return List.of(neuronsInLayer);
        }
    }

}
