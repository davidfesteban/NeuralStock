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
}