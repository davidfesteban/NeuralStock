export class AlgorithmBoard {
    constructor(inputSize, outputSize, learningRatio, complexity, tridimensional, algorithmType, shape, shapeFigure) {
        this.inputSize = inputSize;  // int
        this.outputSize = outputSize;  // int
        this.learningRatio = learningRatio;  // double as number
        this.complexity = complexity;  // double as number
        this.tridimensional = tridimensional;  // boolean
        this.algorithmType = algorithmType;  // String
        this.shape = shape;  // String
        this.shapeFigure = shapeFigure;  // Array<Array<number>>
    }

    static fromJson(jsonObj) {
        return new AlgorithmBoard(
            jsonObj.inputSize,
            jsonObj.outputSize,
            jsonObj.learningRatio,
            jsonObj.complexity,
            jsonObj.tridimensional,
            jsonObj.algorithmType,
            jsonObj.shape,
            jsonObj.shapeFigure.map(innerArray => [...innerArray])
        );
    }

    equals(other) {
        if (!(other instanceof AlgorithmBoard)) {
            return false;
        }

        return (
            this.inputSize === other.inputSize &&
            this.outputSize === other.outputSize &&
            this.learningRatio === other.learningRatio &&
            this.complexity === other.complexity &&
            this.tridimensional === other.tridimensional &&
            this.algorithmType === other.algorithmType &&
            this.shape === other.shape &&
            this.arrayOfArraysEquals(this.shapeFigure, other.shapeFigure)
        );
    }

    arrayOfArraysEquals(arr1, arr2) {
        if (!Array.isArray(arr1) || !Array.isArray(arr2) || arr1.length !== arr2.length) {
            return false;
        }

        return arr1.every((subArray, index) =>
            Array.isArray(subArray) &&
            Array.isArray(arr2[index]) &&
            subArray.length === arr2[index].length &&
            subArray.every((val, idx) => val === arr2[index][idx])
        );
    }
}