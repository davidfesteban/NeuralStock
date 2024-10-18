import {AlgorithmBoard} from "./AlgorithmBoard.js";
import {Status} from "./Status.js";

export class NetworkBoard {
    constructor(networkId, algorithmBoard, status) {
        this.networkId = networkId || crypto.randomUUID();  // UUID
        this.algorithmBoard = algorithmBoard;  // AlgorithmBoard instance
        this.status = status; //Status
    }

    static fromJson(jsonObj) {
        return new NetworkBoard(
            jsonObj.networkId,
            AlgorithmBoard.fromJson(jsonObj.algorithmBoard),
            Status.fromJson(jsonObj.status)
        );
    }

    equals(other) {
        if (!(other instanceof NetworkBoard)) {
            return false;
        }

        return (
            this.networkId === other.networkId &&
            this.algorithmBoard.equals(other.algorithmBoard) &&
            this.status.equals(other.status)
        );
    }
}