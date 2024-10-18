export class DataPair {
    constructor(uuid, topic, createdAt, inputs, expected) {
        this.uuid = uuid || crypto.randomUUID();  // UUID string
        this.topic = topic;  // String
        this.createdAt = createdAt || Date.now();  // Timestamp in milliseconds (int64)
        this.inputs = inputs;  // Array of numbers (doubles)
        this.expected = expected;
    }

    static fromJson(jsonObj) {
        return new DataPair(
            jsonObj.uuid,
            jsonObj.topic,
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
            this.topic === other.topic &&
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