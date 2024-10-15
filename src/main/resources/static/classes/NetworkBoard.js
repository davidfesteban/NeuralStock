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

    equals(other) {
        if (!(other instanceof NetworkBoard)) {
            return false;
        }

        return (
            this.networkId === other.networkId &&
            this.algorithmBoard.equals(other.algorithmBoard) &&
            this.status.equals(other.status) &&
            this.datasetSize === other.datasetSize &&
            this.predictionsSize === other.predictionsSize
        );
    }
}