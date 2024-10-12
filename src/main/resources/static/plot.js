
// Global
let currentBoardId;
let networkBoards;
let predictions;
let plotlyLibrary;

// Trigger fetchAllNetworks when the page loads
window.onload = function() {
    //SSE Emitter for fetching all Network Boards & Status
    fetchAllNetworks();

    //Outside here, a button to load the technical charts with a loader
};

//API Fetch
function fetchAllNetworks() {
    const eventSource = new EventSource('/events');

    eventSource.onmessage = function(event) {
        console.log("Received event: ", event.data);
        networkBoards = event.data.map(network => NetworkBoard.fromJson(network));
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
}


function reloadBoard(networkBoard) {

    mapToDOM(networkBoard);
    fetchPlot(networkId);

    // Construct the URL with the networkId as a query parameter
    const url = `/reloadBoard?networkId=${networkId}`;

    // Make a GET request to the /reloadBoard endpoint
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return NetworkBoard.fromJson(response.json());  // Parse the response as JSON
        })
        .then(networkBoard => {
            console.log("Reloaded NetworkBoard:", networkBoard);

            document.getElementById("network-id").innerText = networkBoard.networkId;
            document.getElementById("network-status").innerText = networkBoard.status;
            document.getElementById("epoch-goal").innerText = networkBoard.epochGoal;
            document.getElementById("current-epoch").innerText = networkBoard.currentEpoch;

            document.getElementById("avg-fitness-error").innerText = networkBoard.avgFitnessError;
            document.getElementById("last-fitness-error").innerText = networkBoard.lastFitnessError;
            document.getElementById("last-training-time").innerText = networkBoard.lastTrainingTime;
            document.getElementById("total-epochs").innerText = networkBoard.totalEpochs;

            document.getElementById("input-size").innerText = networkBoard.algorithmBoard.inputSize;
            document.getElementById("output-size").innerText = networkBoard.algorithmBoard.outputSize;
            document.getElementById("learning-ratio").innerText = networkBoard.algorithmBoard.learningRatio;
            document.getElementById("complexity").innerText = networkBoard.algorithmBoard.complexity;
            document.getElementById("tridimensional").innerText = networkBoard.algorithmBoard.tridimensional;
            document.getElementById("algorithm-type").innerText = networkBoard.algorithmBoard.algorithmType;
            document.getElementById("shape-type").innerText = networkBoard.algorithmBoard.shape;

            //List<List<Integer>> shapeFigure;

            const trace = {
                x: Array.from({ length: networkBoard.mseErrors.length }, (_, i) => i + 1),  // x-axis: epochs or indexes
                y: networkBoard.mseErrors,  // y-axis: mse errors
                type: 'scatter',  // Use a line chart (scatter plot with connected lines)
                mode: 'lines+markers',
                marker: { color: 'blue' },
                line: { shape: 'linear' }
            };

            const layout = {
                title: 'MSE Errors Over Epochs',
                xaxis: {
                    title: 'Epoch',
                    showgrid: false,
                    zeroline: false
                },
                yaxis: {
                    title: 'MSE Error',
                    showline: false
                }
            };

            const data = [trace];

            // Render the plot inside the div with id "plotly-mse-errors"
            //Plotly.newPlot('plotly-mse-errors', data, layout);



        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

// Function to get all predictions for a given networkId
function getAllPredictions(networkId) {
    // Construct the URL with the networkId as a query parameter
    const url = `/getAllPredictions?networkId=${networkId}`;

    // Make a GET request to the /getAllPredictions endpoint
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();  // Parse the response as JSON
        })
        .then(predictedDataList => {
            console.log("All Predictions:", predictedDataList);
            // You can now use the predictedDataList object in your application
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

async function loadPlotly() {
    try {
        plotlyLibrary = await import("https://cdn.plot.ly/plotly-latest.min.js");

        // Access Plotly from the global window object
        const Plotly = window.Plotly;

        const data = [{
            x: [1, 2, 3, 4, 5],
            y: [1, 9, 4, 7, 5]
        }];

        const layout = {
            title: 'Plotly Example Chart'
        };

        Plotly.newPlot('chart', data, layout);
    } catch (error) {
        console.error('Failed to load Plotly:', error);
    }
}

//Classes
class NetworkBoard {
    constructor(
        networkId,
        totalEpochs,
        totalTrainingTime,
        algorithmBoard,
        status,
        epochGoal,
        currentEpoch,
        avgFitnessError,
        lastFitnessError,
        lastTrainingTime,
        avgFitnessWithInEpochs,
        mseErrors
    ) {
        this.networkId = networkId;
        this.totalEpochs = totalEpochs;
        this.totalTrainingTime = totalTrainingTime;
        this.algorithmBoard = algorithmBoard;
        this.status = status;
        this.epochGoal = epochGoal;
        this.currentEpoch = currentEpoch;
        this.avgFitnessError = avgFitnessError;
        this.lastFitnessError = lastFitnessError;
        this.lastTrainingTime = lastTrainingTime;
        this.avgFitnessWithInEpochs = avgFitnessWithInEpochs;
        this.mseErrors = mseErrors;
    }

    // Static method to deserialize a plain object into a NetworkBoard instance
    static fromJson(jsonObj) {
        return new NetworkBoard(
            jsonObj.networkId,
            jsonObj.totalEpochs,
            jsonObj.totalTrainingTime,
            AlgorithmBoard.fromJson(jsonObj.algorithmBoard),  // You can further process this if it's an object
            jsonObj.status,
            jsonObj.epochGoal,
            jsonObj.currentEpoch,
            jsonObj.avgFitnessError,
            jsonObj.lastFitnessError,
            jsonObj.lastTrainingTime,
            jsonObj.avgFitnessWithInEpochs,
            jsonObj.mseErrors
        );
    }
}

class AlgorithmBoard {
    constructor(inputSize, outputSize, learningRatio, complexity, tridimensional, algorithmType, shape, shapeFigure) {
        this.inputSize = inputSize;  // Integer as number
        this.outputSize = outputSize;  // Integer as number
        this.learningRatio = learningRatio;  // double as number
        this.complexity = complexity;  // double as number
        this.tridimensional = tridimensional;  // boolean
        this.algorithmType = algorithmType;  // String
        this.shape = shape;  // String
        this.shapeFigure = shapeFigure;  // List<List<Integer>> as Array<Array<number>>
    }

    // Static method to deserialize a plain object into an AlgorithmBoard instance
    static fromJson(jsonObj) {
        return new AlgorithmBoard(
            jsonObj.inputSize,
            jsonObj.outputSize,
            jsonObj.learningRatio,
            jsonObj.complexity,
            jsonObj.tridimensional,
            jsonObj.algorithmType,
            jsonObj.shape,
            jsonObj.shapeFigure
        );
    }
}

class PredictedData {
    constructor(uuid, createdAt, networkId, epochHappened, predicted, inputs, expected) {
        this.uuid = uuid;  // UUID as string
        this.createdAt = createdAt;  // long as number (timestamp)
        this.networkId = networkId;  // UUID as string
        this.epochHappened = epochHappened;  // int as number
        this.predicted = predicted;  // List<Double> as Array<number>
        this.inputs = inputs;  // List<Double> as Array<number>
        this.expected = expected;  // List<Double> as Array<number>
    }

    // Static method to deserialize a plain object into a PredictedData instance
    static fromJson(jsonObj) {
        return new PredictedData(
            jsonObj.uuid,  // UUID as string
            jsonObj.createdAt,  // long as timestamp
            jsonObj.networkId,  // UUID as string
            jsonObj.epochHappened,
            jsonObj.predicted,  // List<Double> as Array<number>
            jsonObj.inputs,  // List<Double> as Array<number>
            jsonObj.expected  // List<Double> as Array<number>
        );
    }

    // You can add additional methods if needed, like comparing or calculating errors.
}