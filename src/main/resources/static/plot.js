
// Global
let currentBoardId;
let networkBoards;
let predictions;

// Trigger fetchAllNetworks when the page loads
window.onload = function() {
    //SSE Emitter for fetching all Network Boards & Status
    listenAllNetworks();

    //Outside here, a button to load the technical charts with a loader
};

function reloadBoard(networkBoard) {
    mapToDOM(networkBoard);
    fetchPlotWithDefinition(networkBoard.networkId, null).then(plotboard => {
        // Update badge values with the last predicted inputs, expected, and predicted values
        document.getElementById("last-input-values").innerText = plotboard.lastEpochPredicted.inputs; // Convert array to string
        document.getElementById("last-expected-values").innerText = plotboard.lastEpochPredicted.expected;
        document.getElementById("last-predicted-values").innerText = plotboard.lastEpochPredicted.predicted;

        //Display on plotly-mse-errors
        // Assuming plotboard.mseErrors is a list of MSE values over epochs
        const mseErrorElement = document.getElementById('plotly-mse-errors');
        const mseData = [{
            x: [...Array(plotboard.mseErrors.length).keys()], // Create an array of epochs
            y: plotboard.mseErrors, // The MSE error values
            mode: 'lines+markers',
            type: 'scatter',
            name: 'MSE Errors'
        }];
        const mseLayout = {
            title: 'MSE Errors Over Epochs',
            xaxis: { title: 'Epoch' },
            yaxis: { title: 'MSE Error' }
        };
        Plotly.newPlot(mseErrorElement, mseData, mseLayout);

        // Display on plotly-neural-shape
        // Assuming networkBoard.status.shapeFigure is a 2D array (shape of the neural network layers)
        const shapeElement = document.getElementById('plotly-neural-shape');
        const shapeData = [{
            z: networkBoard.status.shapeFigure,
            type: 'heatmap',
            colorscale: 'Viridis', // Use a nice color scheme for heatmaps
            name: 'Neural Network Shape'
        }];
        const shapeLayout = {
            title: 'Neural Network Shape',
            xaxis: { title: 'Neuron Index' },
            yaxis: { title: 'Layer Index' }
        };
        Plotly.newPlot(shapeElement, shapeData, shapeLayout);
    })
}

function mapToDOM(networkBoard) {
    console.log("Reloaded NetworkBoard:", networkBoard.networkId);

    document.getElementById("network-id").innerText = networkBoard.networkId;
    document.getElementById("network-status").innerText = networkBoard.status.running;
    document.getElementById("epoch-goal").innerText = networkBoard.status.goalEpochs;
    document.getElementById("current-epoch").innerText = networkBoard.status.currentEpochToGoal;

    //document.getElementById("avg-fitness-error").innerText = networkBoard.avgFitnessError;
    //document.getElementById("last-fitness-error").innerText = networkBoard.lastFitnessError;
    //document.getElementById("last-training-time").innerText = networkBoard.lastTrainingTime;
    document.getElementById("total-epochs").innerText = networkBoard.status.accumulatedEpochs;

    document.getElementById("input-size").innerText = networkBoard.algorithmBoard.inputSize;
    document.getElementById("output-size").innerText = networkBoard.algorithmBoard.outputSize;
    document.getElementById("learning-ratio").innerText = networkBoard.algorithmBoard.learningRatio;
    document.getElementById("complexity").innerText = networkBoard.algorithmBoard.complexity;
    document.getElementById("tridimensional").innerText = networkBoard.algorithmBoard.tridimensional;
    document.getElementById("algorithm-type").innerText = networkBoard.algorithmBoard.algorithmType;
    document.getElementById("shape-type").innerText = networkBoard.algorithmBoard.shape;
    //plotly shape figure

    document.getElementById("dataset-size").innerText = networkBoard.status.datasetSize;
    document.getElementById("predictions-size").innerText = networkBoard.status.predictionsSize;

}

//API Client

// Function to get all networks using Server-Sent Events (SSE)
function listenAllNetworks() {
    const eventSource = new EventSource('/getAllNetworks');

    eventSource.onmessage = function(event) {
        console.log("Received event: ", event.data);
        const parsedData = JSON.parse(event.data);
        networkBoards = parsedData.map(network => NetworkBoard.fromJson(network));
        const dropdown = document.getElementById("network-uuid-list");
        dropdown.innerHTML = '';

        networkBoards.forEach(networkBoard => {
            const li = document.createElement('li');
            const a = document.createElement('a');

            a.classList.add('dropdown-item');
            a.href = '#';  // You can change this if needed for actual navigation
            a.textContent = networkBoard.networkId;

            if(currentBoardId === networkBoard.networkId) {
                reloadBoard(networkBoard);
            }

            a.addEventListener('click', () => {
                currentBoardId = networkBoard.networkId;
                reloadBoard(networkBoard);
            });

            li.appendChild(a);
            dropdown.appendChild(li);
        });
    };

    eventSource.onerror = () => {
        eventSource.close();
    };

    return eventSource;
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



//Classes
class PlotBoard {
    constructor(lastEpochPredicted, mseErrors) {
        this.lastEpochPredicted = lastEpochPredicted;  // Array of PredictedData
        this.mseErrors = mseErrors;  // Array<number>
    }

    static fromJson(jsonObj) {
        return new PlotBoard(
            jsonObj.lastEpochPredicted.map(item => PredictedData.fromJson(item)),
            jsonObj.mseErrors
        );
    }
}

class PredictedData {
    constructor(uuid, createdAt, networkId, epochHappened, predicted, inputs, expected) {
        this.uuid = uuid || crypto.randomUUID();  // UUID
        this.createdAt = createdAt || Date.now();  // long as number
        this.networkId = networkId;  // UUID
        this.epochHappened = epochHappened;  // int
        this.predicted = predicted;  // Array<number>
        this.inputs = inputs;  // Array<number>
        this.expected = expected;  // Array<number>
    }

    static fromJson(jsonObj) {
        return new PredictedData(
            jsonObj.uuid,
            jsonObj.createdAt,
            jsonObj.networkId,
            jsonObj.epochHappened,
            jsonObj.predicted,
            jsonObj.inputs,
            jsonObj.expected
        );
    }
}

class NetworkBoard {
    constructor(networkId, algorithmBoard, status, datasetSize, predictionsSize) {
        this.networkId = networkId || crypto.randomUUID();  // UUID
        this.algorithmBoard = algorithmBoard;  // AlgorithmBoard instance
        this.status = status; //Status
        this.datasetSize = datasetSize;  // int
        this.predictionsSize = predictionsSize;  // int
    }

    static fromJson(jsonObj) {
        return new NetworkBoard(
            jsonObj.networkId,
            AlgorithmBoard.fromJson(jsonObj.algorithmBoard),
            Status.fromJson(jsonObj.status),
            jsonObj.datasetSize,
            jsonObj.predictionsSize
        );
    }
}

class AlgorithmBoard {
    constructor(inputSize, outputSize, learningRatio, complexity, tridimensional, algorithmType, shape, shapeFigure) {
        this.inputSize = inputSize;  // int
        this.outputSize = outputSize;  // int
        this.learningRatio = learningRatio;  // double as number
        this.complexity = complexity;  // double as number
        this.tridimensional = tridimensional;  // boolean
        this.algorithmType = algorithmType;  // String
        this.shape = shape;  // String
        this.shapeFigure = shapeFigure;  // Array<Array<number>>
    }

    static fromJson(jsonObj) {
        return new AlgorithmBoard(
            jsonObj.inputSize,
            jsonObj.outputSize,
            jsonObj.learningRatio,
            jsonObj.complexity,
            jsonObj.tridimensional,
            jsonObj.algorithmType,
            jsonObj.shape,
            jsonObj.shapeFigure.map(innerArray => [...innerArray])
        );
    }
}

class DataPair {
    constructor(uuid, createdAt, networkId, inputs, expected) {
        this.uuid = uuid || crypto.randomUUID();  // UUID
        this.createdAt = createdAt || Date.now();  // long as number
        this.networkId = networkId;  // UUID
        this.inputs = inputs;  // Array<number>
        this.expected = expected;  // Array<number>
    }

    static fromJson(jsonObj) {
        return new DataPair(
            jsonObj.uuid,
            jsonObj.createdAt,
            jsonObj.networkId,
            jsonObj.inputs,
            jsonObj.expected
        );
    }
}

class Status {
    constructor(networkId, running, accumulatedEpochs, trainingId, goalEpochs, currentEpochToGoal) {
        this.networkId = networkId || crypto.randomUUID();  // UUID
        this.running = running;  // boolean
        this.accumulatedEpochs = accumulatedEpochs;  // int
        this.trainingId = trainingId || null;  // UUID
        this.goalEpochs = goalEpochs;  // int
        this.currentEpochToGoal = currentEpochToGoal;  // int
    }

    static fromJson(jsonObj) {
        return new Status(
            jsonObj.networkId,
            jsonObj.running,
            jsonObj.accumulatedEpochs,
            jsonObj.trainingId,
            jsonObj.goalEpochs,
            jsonObj.currentEpochToGoal
        );
    }
}