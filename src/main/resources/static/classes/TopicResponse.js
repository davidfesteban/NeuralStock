export class TopicResponse {
    constructor(topic, count) {
        this.topic = topic;
        this.count = count;
    }

    static fromJson(jsonObj) {
        return new TopicResponse(
            jsonObj.topic,
            jsonObj.count
        );
    }
}
