import {TopicResponse} from "../classes/TopicResponse.js";

export class DataSetAPI {

    constructor(baseUrl) {
        this.baseUrl = baseUrl
    }

    async includeDataSet(dataSet, callback) {
        return oboe({
            url: `${this.baseUrl}/api/dataset/add`,
            method: 'POST',
            body: JSON.stringify(dataSet),  // Assuming dataSet is an array of DataPair objects
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function () {
                callback();
                console.log('Received IncludeDataSet');
            })
            .done(function () {
                console.log('IncludeDataSet Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    async removeDataSetByUUID(dataSetUUIDs, callback) {
        return oboe({
            url: `${this.baseUrl}/api/dataset/removeList`,
            method: 'POST',
            body: JSON.stringify(dataSetUUIDs),  // Assuming dataSetUUIDs is an array of UUIDs
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function () {
                callback();
                console.log('Received RemoveDataSetByUUID');
            })
            .done(function () {
                console.log('RemoveDataSetByUUID Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    async removeDataSetByTopic(topic, callback) {
        return oboe({
            url: `${this.baseUrl}/api/dataset/removeTopic?topic=${topic}`,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function () {
                callback();
                console.log('Received RemoveDataSetByTopic');
            })
            .done(function () {
                console.log('RemoveDataSetByTopic Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    async getAllTopics(callback) {
        return oboe({
            url: `${this.baseUrl}/api/dataset/getAllTopics`,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function (topics) {
                callback(topics.map(topic => TopicResponse.fromJson(topic)));
                console.log('Received topics:', topics);
            })
            .done(function () {
                console.log('GetAllTopics Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }
}