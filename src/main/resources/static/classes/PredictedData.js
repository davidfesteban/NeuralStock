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
}