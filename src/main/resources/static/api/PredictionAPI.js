
import {PredictedData} from "../classes/PredictedData.js";

export class PredictionAPI {

    constructor(baseUrl) {
        this.baseUrl = baseUrl
    }

    async compute(topic, networkId, epochs, createdAtStart = null, createdAtEnd = null, lastAmount = null) {
        const url = new URL(`${this.baseUrl}/api/prediction/compute`);
        url.searchParams.append('topic', topic);
        url.searchParams.append('networkId', networkId);
        url.searchParams.append('epochs', epochs);
        if (createdAtStart) url.searchParams.append('createdAtStart', createdAtStart);
        if (createdAtEnd) url.searchParams.append('createdAtEnd', createdAtEnd);
        if (lastAmount) url.searchParams.append('lastAmount', lastAmount);

        return oboe({
            url: url,
            method: 'GET',
            //body: JSON.stringify(algorithmBoard),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function () {
                console.log('Received compute');
            })
            .done(function () {
                console.log('compute Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    async predict(networkId, dataSet, callback) {
        const url = new URL(`${this.baseUrl}/api/prediction/predict`);
        url.searchParams.append('networkId', networkId);

        return oboe({
            url: url,
            method: 'POST',
            body: JSON.stringify(dataSet),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function (predictedDataJson) {
                callback(PredictedData.fromJson(predictedDataJson))
                console.log('Received predict');
            })
            .done(function () {
                console.log('predict Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }
}