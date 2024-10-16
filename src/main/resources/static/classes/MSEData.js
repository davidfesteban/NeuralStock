export class MSEData {
    constructor(networkId, epochHappened, error) {
        this.networkId = networkId;  // UUID
        this.epochHappened = epochHappened;  // int
        this.error = error;  // double as number
    }

    static fromJson(jsonObj) {
        return new MSEData(
            jsonObj.networkId,
            jsonObj.epochHappened,
            jsonObj.error
        );
    }

    equals(other) {
        if (!(other instanceof MSEData)) {
            return false;
        }

        return (
            this.networkId === other.networkId &&
            this.epochHappened === other.epochHappened &&
            this.error === other.error
        );
    }
}