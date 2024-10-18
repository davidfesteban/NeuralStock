import {DataSetAPI} from "./api/DataSetAPI.js";
import {MetricsAPI} from "./api/MetricsAPI.js";
import {NetworkAPI} from "./api/NetworkAPI.js";
import {PredictionAPI} from "./api/PredictionAPI.js";

let API_BASE_URL = 'http://localhost:8080';

export class ApiClient {


    constructor(baseURL) {
        API_BASE_URL = baseURL? baseURL : API_BASE_URL;
        this.dataSetAPI = new DataSetAPI(API_BASE_URL);
        this.metricsAPI = new MetricsAPI(API_BASE_URL);
        this.networkAPI = new NetworkAPI(API_BASE_URL);
        this.predictionAPI = new PredictionAPI(API_BASE_URL);
    }
}