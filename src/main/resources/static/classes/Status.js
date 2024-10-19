export class Status {
    constructor(networkId, running, accumulatedEpochs, trainingId, goalEpochs, currentEpochToGoal) {
        this.networkId = networkId || crypto.randomUUID();  // UUID
        this.running = running;  // boolean
        this.accumulatedEpochs = accumulatedEpochs;  // int
        this.trainingId = trainingId || null;  // UUID
        this.goalEpochs = goalEpochs;  // int
    }

    static fromJson(jsonObj) {
        return new Status(
            jsonObj.networkId,
            jsonObj.running,
            jsonObj.accumulatedEpochs,
            jsonObj.trainingId,
            jsonObj.goalEpochs
        );
    }

    equals(other) {
        if (!(other instanceof Status)) {
            return false;
        }

        return (
            this.networkId === other.networkId &&
            this.running === other.running &&
            this.accumulatedEpochs === other.accumulatedEpochs &&
            this.trainingId === other.trainingId &&
            this.goalEpochs === other.goalEpochs
        );
    }
}