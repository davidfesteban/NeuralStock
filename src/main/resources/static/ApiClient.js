import {PredictedData} from "./classes/PredictedData.js";
import {NetworkBoard} from "./classes/NetworkBoard.js";
import {DataPair} from "./classes/DataPair.js";
import {UUIDResponse} from "./classes/UUIDResponse.js";
import {MSEData} from "./classes/MSEData.js";

let API_BASE_URL = 'http://localhost:8080';

export class ApiClient {
    constructor(baseURL) {
        API_BASE_URL = baseURL;
    }

    // DataSet API: POST /api/dataset/remove
    async includeDataSet(networkId, dataSet) {
        return oboe({
            url: `${API_BASE_URL}/api/dataset/add?networkId=${networkId}`,
            method: 'POST',
            body: JSON.stringify(dataSet),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function () {
                console.log('Received IncludeDataSet');
            })
            .done(function () {
                console.log('IncludeDataSet Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    // DataSet API: POST /api/dataset/remove
    async removeDataSet(uuidList) {
        return oboe({
            url: `${API_BASE_URL}/api/dataset/remove`,
            method: 'POST',
            body: JSON.stringify(uuidList),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function () {
                console.log('Received RemoveDataSet');
            })
            .done(function () {
                console.log('RemoveDataSet Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    async getAllDataSet(networkId, dataSet, callback) {
        return oboe({
            url: `${API_BASE_URL}/api/dataset/getAll?networkId=${networkId}`,
            method: 'GET',
            body: JSON.stringify(dataSet),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function (dataPair) {
                callback(DataPair.fromJson(dataPair));
                console.log('Received getAll DataPair');
            })
            .done(function () {
                console.log('DataPair getAll Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    // Network API
    async createNetwork(algorithmBoard, callback) {
        return oboe({
            url: `${API_BASE_URL}/api/network/create`,
            method: 'POST',
            body: JSON.stringify(algorithmBoard),
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function (uuid) {
                console.log('Received createNetwork ', uuid);
                callback(UUIDResponse.fromJson(uuid));
            })
            .done(function (response) {
                console.log('Response:', response);
                console.log('createNetwork Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    async getAllNetworks(callback) {
        return oboe({
            url: `${API_BASE_URL}/api/network/getAll`,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('![*]', function (networkBoardJson) {
                console.log(networkBoardJson);
                callback(NetworkBoard.fromJson(networkBoardJson));
                console.log('Received getAllNetworks');
            })
            .done(function () {
                console.log('getAllNetworks Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }

    async deleteEntireNetwork(networkId) {
        return oboe({
            url: `${API_BASE_URL}/api/network/deleteEntire?networkId=${networkId}`,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('!', function () {
                console.log('Received deleteEntireNetwork');
            })
            .done(function () {
                console.log('deleteEntireNetwork Stream completed');
            })
            .fail(function (err) {
                console.error('Error occurred:', err);
            });
    }


    // Prediction API: POST /api/prediction/predict  return List<PredictedData>
    async compute(networkId, epochs, createdAtStart = null, createdAtEnd = null, lastAmount = null) {
        const url = new URL(`${API_BASE_URL}/api/prediction/compute`);
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
        const url = new URL(`${API_BASE_URL}/api/prediction/predict`);
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

    async getPredictionsNotOboe(networkId, lastEpochAmount, onStartCallBack, callback, onFinishCallBack) {
        const url = new URL(`${API_BASE_URL}/api/prediction/getPredictionsWithDefinition`);
        url.searchParams.append('networkId', networkId);
        if (lastEpochAmount) {
            url.searchParams.append('lastEpochAmount', lastEpochAmount);
        }
        onStartCallBack();
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');
        let buffer = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });

            try {
                // Attempt to parse the buffer into valid JSON
                let json = JSON.parse(buffer);

                // If parsing succeeds, pass the data to the callback
                callback(json);

                // Reset the buffer after successful parsing
                buffer = '';
            } catch (e) {
                // If parsing fails, continue reading more chunks
                // This means the buffer is incomplete, and we wait for more data
            }
        }

        onFinishCallBack();
        console.log('getPredictions Stream completed');
    }

    async getPredictions(networkId, lastEpochAmount, downsample, onStart, callback, terminatedCallback) {
        const url = new URL(`${API_BASE_URL}/api/prediction/getPredictionsWithDefinition`);
        url.searchParams.append('networkId', networkId);
        if (lastEpochAmount) url.searchParams.append('lastEpochAmount', lastEpochAmount);
        if (downsample) url.searchParams.append('downsample', downsample);

        onStart();
        return oboe({
            url: url,
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .node('![*]', function (predictedDataJson) {
                callback(PredictedData.fromJson(predictedDataJson));
                console.log('Received getPredictions');
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

    async getMSEData(networkId, onStart, callback, terminatedCallback) {
        const url = new URL(`${API_BASE_URL}/api/metrics/mseData`);
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