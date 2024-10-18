import {UUIDResponse} from "../classes/UUIDResponse.js";
import {NetworkBoard} from "../classes/NetworkBoard.js";


export class NetworkAPI {

    constructor(baseUrl) {
        this.baseUrl = baseUrl
    }

    async createNetwork(algorithmBoard, callback) {
        return oboe({
            url: `${this.baseUrl}/api/network/create`,
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

    async getAllNetworkBoard(callback) {
        return oboe({
            url: `${this.baseUrl}/api/network/getAllSummary`,
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
}