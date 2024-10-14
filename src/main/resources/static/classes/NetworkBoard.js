import {AlgorithmBoard} from "./AlgorithmBoard.js";
import {Status} from "./Status.js";

export class NetworkBoard {
    constructor(networkId, algorithmBoard, status, datasetSize, predictionsSize) {
        this.networkId = networkId || crypto.randomUUID();  // UUID
        this.algorithmBoard = algorithmBoard;  // AlgorithmBoard instance
        this.status = status; //Status
        this.datasetSize = datasetSize;  // int
        this.predictionsSize = predictionsSize;  // int
    }

    static fromJson(jsonObj) {
        return new NetworkBoard(
            jsonObj.networkId,
            AlgorithmBoard.fromJson(jsonObj.algorithmBoard),
            Status.fromJson(jsonObj.status),
            jsonObj.datasetSize,
            jsonObj.predictionsSize
        );
    }
}