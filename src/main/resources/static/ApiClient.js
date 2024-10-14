import {PredictedData} from "./classes/PredictedData.js";
import {NetworkBoard} from "./classes/NetworkBoard.js";
import {PlotBoard} from "./classes/PlotBoard.js";

let API_BASE_URL = 'http://localhost:8080';

export class ApiClient {
    constructor(baseURL) {
        API_BASE_URL = baseURL;
    }

    // Prediction API: POST /api/prediction/predict  return List<PredictedData>
    async predict(networkId, dataPairs) {
        const url = `${API_BASE_URL}/api/prediction/predict?networkId=${networkId}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dataPairs)
        });
        return (await response.json()).map(element => PredictedData.fromJson(element));
    }

    // Network API: POST /api/network/upload Only as a form
    async uploadJsonFile(file) {
        const url = `${API_BASE_URL}/api/network/upload`;
        const formData = new FormData();
        formData.append('file', file);

        const response = await fetch(url, {
            method: 'POST',
            body: formData
        });
        return response.json();
    }

    // Network API: POST /api/network/create Returns UUID
    async createNetwork(algorithmBoard) {
        const url = `${API_BASE_URL}/api/network/create`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(algorithmBoard)
        });

        return response.json();
    }

    // DataSet API: POST /api/dataset/remove
    async removeDataSet(dataSetIds) {
        const url = `${API_BASE_URL}/api/dataset/remove`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dataSetIds)
        });
        return await response.json();
    }

    // DataSet API: POST /api/dataset/add
    async includeDataSet(networkId, dataPairs) {
        const url = `${API_BASE_URL}/api/dataset/add?networkId=${networkId}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dataPairs)
        });
        return await response.json();
    }

    // Scheduler API: GET /api/scheduler/buffer
    async modifyBuffer(size) {
        const url = `${API_BASE_URL}/api/scheduler/buffer?size=${size}`;
        const response = await fetch(url, { method: 'GET' });
        return await response.json();
    }

    // Prediction API: GET /api/prediction/getPredictionsWithDefinition
    async getPredictions(networkId, lastEpochAmount = null) {
        const url = new URL(`${API_BASE_URL}/api/prediction/getPredictionsWithDefinition`);
        url.searchParams.append('networkId', networkId);
        if (lastEpochAmount) {
            url.searchParams.append('lastEpochAmount', lastEpochAmount);
        }

        const response = await fetch(url, { method: 'GET' });
        return await response.json();
    }

    // Prediction API: GET /api/prediction/compute
    async compute(networkId, epochs, createdAtStart = null, createdAtEnd = null, lastAmount = null) {
        const url = new URL(`${API_BASE_URL}/api/prediction/compute`);
        url.searchParams.append('networkId', networkId);
        url.searchParams.append('epochs', epochs);
        if (createdAtStart) url.searchParams.append('createdAtStart', createdAtStart);
        if (createdAtEnd) url.searchParams.append('createdAtEnd', createdAtEnd);
        if (lastAmount) url.searchParams.append('lastAmount', lastAmount);

        const response = await fetch(url, { method: 'GET' });
        return await response.json();
    }

    // Plot API: GET /api/plot/fetchPlotWithDefinition
    async fetchPlot(networkId, lastEpochAmount = null) {
        const url = new URL(`${API_BASE_URL}/api/plot/fetchPlotWithDefinition`);
        url.searchParams.append('networkId', networkId);
        if (lastEpochAmount) {
            url.searchParams.append('lastEpochAmount', lastEpochAmount);
        }

        const response = await fetch(url, { method: 'GET' });
        const json = await response.json();
        console.log(json);
        return PlotBoard.fromJson(json);
    }

    // Network API: GET /api/network/reloadFromFiles
    async reloadFromFiles() {
        const url = `${API_BASE_URL}/api/network/reloadFromFiles`;
        const response = await fetch(url, { method: 'GET' });
        return await response.json();
    }

    // Network API: GET /api/network/getAllNetworks (SSE Example)
    getAllNetworksSSE(onMessage, onError, onComplete) {
        const url = `${API_BASE_URL}/api/network/getAllNetworks`;

        const eventSource = new EventSource(url);

        // Event handler for new messages
        eventSource.onmessage = (event) => {
            if (onMessage) onMessage(JSON.parse(event.data).map(network => NetworkBoard.fromJson(network)));
        };

        // Event handler for errors
        eventSource.onerror = (error) => {
            if (onError) onError(error);
            eventSource.close(); // Close the SSE connection
        };

        // Optional: Handle open or close events
        eventSource.onopen = () => {
            console.log('SSE connection opened');
        };

        return eventSource; // Return the EventSource so it can be closed if needed
    }

    // Network API: GET /api/network/download
    async downloadNetwork(networkId) {
        const url = `${API_BASE_URL}/api/network/download?networkId=${networkId}`;
        const response = await fetch(url, { method: 'GET' });
        return await response.blob(); // Assuming it's a binary file
    }

    // Network API: GET /api/network/deleteEntire
    async deleteNetwork(networkId) {
        const url = `${API_BASE_URL}/api/network/deleteEntire?networkId=${networkId}`;
        const response = await fetch(url, { method: 'GET' });
        return await response.json();
    }

    // DataSet API: GET /api/dataset/getAll
    async getAllDataSets(networkId, page = null) {
        const url = new URL(`${API_BASE_URL}/api/dataset/getAll`);
        url.searchParams.append('networkId', networkId);
        if (page) {
            url.searchParams.append('page', page);
        }

        const response = await fetch(url, { method: 'GET' });
        return await response.json();
    }
}


// Function to fetch the plot with network definition
async function fetchPlotWithDefinition(networkId, lastEpochAmount) {
    const url = new URL('/fetchPlotWithDefinition', window.location.origin);
    url.searchParams.append('networkId', networkId);
    if (lastEpochAmount) url.searchParams.append('lastEpochAmount', lastEpochAmount);

    const response = await fetch(url);

    if (!response.ok) {
        throw new Error('Failed to fetch plot');
    }

    return PlotBoard.fromJson(await response.json());  // PlotBoard data
}

// Function to get predictions for a network
async function getPredictions(networkId, lastEpochAmount) {
    const url = new URL('/getPredictionsWithDefinition', window.location.origin);
    url.searchParams.append('networkId', networkId);
    if (lastEpochAmount) url.searchParams.append('lastEpochAmount', lastEpochAmount);

    const response = await fetch(url);

    if (!response.ok) {
        throw new Error('Failed to get predictions');
    }

    return await response.json();  // List<PredictedData>
}