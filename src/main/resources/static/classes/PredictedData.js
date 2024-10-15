export class PredictedData {
    constructor(uuid, createdAt, networkId, epochHappened, predicted, inputs, expected, mseError) {
        this.uuid = uuid || crypto.randomUUID();  // UUID
        this.createdAt = createdAt || Date.now();  // long as number
        this.networkId = networkId;  // UUID
        this.epochHappened = epochHappened;  // int
        this.predicted = predicted;  // Array<number>
        this.inputs = inputs;  // Array<number>
        this.expected = expected;  // Array<number>
        this.mseError = mseError;
    }

    static fromJson(jsonObj) {
        return new PredictedData(
            jsonObj.uuid,
            jsonObj.createdAt,
            jsonObj.networkId,
            jsonObj.epochHappened,
            jsonObj.predicted,
            jsonObj.inputs,
            jsonObj.expected,
            jsonObj.mseError
        );
    }

    equals(other) {
        if (!(other instanceof PredictedData)) {
            return false;
        }

        return (
            this.uuid === other.uuid &&
            this.createdAt === other.createdAt &&
            this.networkId === other.networkId &&
            this.epochHappened === other.epochHappened &&
            this.arrayEquals(this.predicted, other.predicted) &&
            this.arrayEquals(this.inputs, other.inputs) &&
            this.arrayEquals(this.expected, other.expected) &&
            this.arrayEquals(this.mseError, other.mseError)
        );
    }

    arrayEquals(arr1, arr2) {
        if (!Array.isArray(arr1) || !Array.isArray(arr2) || arr1.length !== arr2.length) {
            return false;
        }
        return arr1.every((val, index) => val === arr2[index]);
    }
}