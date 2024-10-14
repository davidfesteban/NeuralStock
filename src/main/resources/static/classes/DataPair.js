export class DataPair {
    constructor(uuid, createdAt, networkId, inputs, expected) {
        this.uuid = uuid || crypto.randomUUID();  // UUID
        this.createdAt = createdAt || Date.now();  // long as number
        this.networkId = networkId;  // UUID
        this.inputs = inputs;  // Array<number>
        this.expected = expected;  // Array<number>
    }

    static fromJson(jsonObj) {
        return new DataPair(
            jsonObj.uuid,
            jsonObj.createdAt,
            jsonObj.networkId,
            jsonObj.inputs,
            jsonObj.expected
        );
    }
}