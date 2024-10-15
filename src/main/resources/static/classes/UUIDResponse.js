export class UUIDResponse {
    constructor(uuid) {
        this.uuid = uuid || crypto.randomUUID();  // UUID
    }

    static fromJson(jsonObj) {
        return new UUIDResponse(
            jsonObj.uuid
        );
    }
}