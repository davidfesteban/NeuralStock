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

    equals(other) {
        if (!(other instanceof DataPair)) {
            return false;
        }

        return (
            this.uuid === other.uuid &&
            this.createdAt === other.createdAt &&
            this.networkId === other.networkId &&
            this.arraysEqual(this.inputs, other.inputs) &&
            this.arraysEqual(this.expected, other.expected)
        );
    }

    arraysEqual(arr1, arr2) {
        if (arr1.length !== arr2.length) {
            return false;
        }
        return arr1.every((value, index) => value === arr2[index]);
    }
}