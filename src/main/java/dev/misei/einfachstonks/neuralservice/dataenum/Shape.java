package dev.misei.einfachstonks.neuralservice.dataenum;

import java.util.ArrayList;
import java.util.List;

public enum Shape {
    PERCEPTRON {
        @Override
        public List<Integer> draw(int input, int output) {
            List<Integer> shape = new ArrayList<>();
            shape.add(input);

            int hiddenLayers = (int) (1 + Math.log10(input));

            int neuronsPerLayer = (int) Math.round(input * 1.20);

            for (int i = 0; i < hiddenLayers; i++) {
                shape.add(neuronsPerLayer);
            }

            shape.add(output);

            display(shape);

            return shape;
        }

    },
    MAGIC {
        @Override
        public List<Integer> draw(int input, int output) {
            List<Integer> shape = new ArrayList<>();
            shape.add(input);

            int hiddenLayers = 3;

            int neuronsPerLayer = 4;

            for (int i = 0; i < hiddenLayers; i++) {
                shape.add(neuronsPerLayer);
            }

            shape.add(output);

            display(shape);

            return shape;
        }
    },
    SQUARE {
        @Override
        public List<Integer> draw(int input, int output) {
            List<Integer> shape = new ArrayList<>();
            shape.add(input);

            int hiddenLayers = (int) (1 + Math.log10(input));

            int neuronsPerLayer = (int) Math.sqrt(input * output);

            for (int i = 0; i < hiddenLayers; i++) {
                shape.add(neuronsPerLayer);
            }

            shape.add(output);

            display(shape);

            return shape;
        }
    },
    TRIANGLE {
        @Override
        public List<Integer> draw(int input, int output) {
            List<Integer> shape = new ArrayList<>();
            shape.add(input);

            int neuronsInCurrentLayer = input;

            while (neuronsInCurrentLayer > output) {

                int neuronsInNextLayer = (int) Math.round(neuronsInCurrentLayer * 0.7);

                if (neuronsInNextLayer <= output) {
                    neuronsInNextLayer = output;
                }

                shape.add(neuronsInNextLayer);
                neuronsInCurrentLayer = neuronsInNextLayer;
            }

            Shape.display(shape);

            return shape;
        }
    },
    COMPRESSOR {
        @Override
        public List<Integer> draw(int input, int output) {
            List<Integer> shape = new ArrayList<>();
            shape.add(input);

            int neuronsInCurrentLayer = input;

            int hiddenLayers = (int) (Math.round(1 + Math.log10(input)) * 3);

            // First half: Compression phase (reduce by 30%)
            for (int i = 0; i < hiddenLayers / 2; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 0.6);
                shape.add(neuronsInCurrentLayer);
            }


            for (int i = hiddenLayers / 2; i < hiddenLayers; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 1.4);
                shape.add(neuronsInCurrentLayer);
            }

            shape.add(output);

            Shape.display(shape);

            return shape;
        }

    },
    EXPANDER {
        @Override
        public List<Integer> draw(int input, int output) {
            List<Integer> shape = new ArrayList<>();
            shape.add(input);

            int neuronsInCurrentLayer = input;

            int hiddenLayers = (int) (Math.round(1 + Math.log10(input)) * 3);

            // First half: Compression phase (reduce by 30%)
            for (int i = 0; i < hiddenLayers / 2; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 1.4);
                shape.add(neuronsInCurrentLayer);
            }


            for (int i = hiddenLayers / 2; i < hiddenLayers; i++) {
                neuronsInCurrentLayer = (int) Math.round(neuronsInCurrentLayer * 0.6);
                shape.add(neuronsInCurrentLayer);
            }

            shape.add(output);

            Shape.display(shape);

            return shape;
        }
    };

    protected static void display(List<Integer> shape) {
        for (int neurons : shape) {
            System.out.println("*".repeat(neurons));
        }
        System.out.println(" ");
    }

    public abstract List<Integer> draw(int inputSize, int outputSize);

}
