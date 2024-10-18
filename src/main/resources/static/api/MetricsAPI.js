import {MSEData} from "../classes/MSEData.js";

export class MetricsAPI {

    constructor(baseUrl) {
        this.baseUrl = baseUrl
    }

    async getMSEData(networkId, onStart, callback, terminatedCallback) {
        const url = new URL(`${this.baseUrl}/api/metrics/mseData`);
        url.searchParams.append('networkId', networkId);

        onStart();
        return oboe({
            url: url,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('![*]', function (mseDataJson) {
                callback(MSEData.fromJson(mseDataJson));
                console.log('Received mseData');
            })
            .done(function () {
                terminatedCallback();
                console.log('getPredictions Stream completed');
            })
            .fail(function (err) {
                terminatedCallback();
                console.error('Error occurred:', err);
            });
    }
}