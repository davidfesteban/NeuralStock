package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.Shape;
import org.junit.jupiter.api.Test;

class ShapeTest {

    @Test
    public void perceptronShapes() {
        Shape.PERCEPTRON.draw(9, 1);
        Shape.PERCEPTRON.draw(5, 2);
        Shape.PERCEPTRON.draw(10, 1);
        Shape.PERCEPTRON.draw(10, 2);
        Shape.PERCEPTRON.draw(100, 2);
        Shape.PERCEPTRON.draw(50, 1);
    }

    @Test
    public void squareShapes() {
        Shape.SQUARE.draw(9, 1);
        Shape.SQUARE.draw(5, 2);
        Shape.SQUARE.draw(10, 1);
        Shape.SQUARE.draw(10, 2);
        Shape.SQUARE.draw(100, 2);
        Shape.SQUARE.draw(50, 1);
        System.out.println("-------------");
    }

    @Test
    public void triangleShapes() {
        Shape.TRIANGLE.draw(10, 1);
        Shape.TRIANGLE.draw(100, 2);
        Shape.TRIANGLE.draw(50, 1);
    }

    @Test
    public void compressorShapes() {
        Shape.COMPRESSOR.draw(10, 1);
        Shape.COMPRESSOR.draw(100, 2);
        Shape.COMPRESSOR.draw(50, 1);
    }

    @Test
    public void expanderShapes() {
        Shape.EXPANDER.draw(10, 1);
        Shape.EXPANDER.draw(30, 2);
        Shape.EXPANDER.draw(50, 1);
    }

}