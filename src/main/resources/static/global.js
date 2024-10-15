import {SummaryCard} from "/component/SummaryCard.js";
import {ApiClient} from "/ApiClient.js";
import {PlotlyMSE} from "/component/PlotlyMSE.js";
import {PlotlyScatterEP} from "/component/PlotlyScatterEP.js";
import {PlotlyMSELast} from "/component/PlotlyMSELast.js";
import {PlotlyShape} from "/component/PlotlyShape.js";

//Initialize Views
customElements.define('summary-card', SummaryCard);
customElements.define('plotly-mse-errors', PlotlyMSE);
customElements.define('plotly-scatter-expected-predicted', PlotlyScatterEP);
customElements.define('plotly-mse-error-last', PlotlyMSELast);
customElements.define('plotly-neural-shape', PlotlyShape);

//Main States
export const apiClient = new ApiClient("http://localhost:8080");
let currentBoardId;
let networkBoardMap = new Map();

//PredictionsOboe
let previousOboeNetworkId;
let predicitonOboeIsOnGoing;
let predicitionOboe;

//PreviousBoard
let previousBoard;

//Window Register
window.onload = async function () {
    setInterval(() => {
        apiClient.getAllNetworks(networkBoard => {
            networkBoardMap.set(networkBoard.networkId, networkBoard);
            if(currentBoardId === networkBoard.networkId) {
                extractCardsDOM(networkBoard);
                extractPlotBoard(networkBoard);
            }
        }).then(() => {
            const dropdown = document.getElementById("network-uuid-list");
            dropdown.innerHTML = '';

            for (let networkId of networkBoardMap.keys()) {
                const li = document.createElement('li');
                const a = document.createElement('a');

                a.classList.add('dropdown-item');
                a.href = '#';
                a.textContent = networkId;

                a.addEventListener('click', () => {
                    currentBoardId = networkId;
                    extractCardsDOM(networkBoardMap.get(networkId));
                    extractPlotBoard(networkBoardMap.get(networkId));
                });

                li.appendChild(a);
                dropdown.appendChild(li);
            }
        })
    }, 5000);
};

function extractCardsDOM(networkBoard) {
    if(previousBoard == null || (!previousBoard.equals(networkBoard))) {
        //TODO: Refactor to ViewModel with bindings
        previousBoard = networkBoard;
        let summaries = [{
            title: "Network Status",
            summary: [["Network Id", networkBoard.networkId], ["Total Epochs", networkBoard.status.accumulatedEpochs],
                ["DataSet Size", networkBoard.datasetSize], ["Predictions Size", networkBoard.predictionsSize],
                ["Input Size", networkBoard.algorithmBoard.inputSize], ["Output Size", networkBoard.algorithmBoard.outputSize]]
        }, {
            title: "Training Metrics",
            summary: [["Status", networkBoard.status.running], ["Epoch Goal", networkBoard.status.goalEpochs],
                ["Current Epoch", networkBoard.status.currentEpochToGoal], ["Training Id", networkBoard.status.trainingId]]
        }, {
            title: "Algorithm Board",
            summary: [["Learning Ratio", networkBoard.algorithmBoard.learningRatio], ["Complexity", networkBoard.algorithmBoard.complexity],
                ["Tridimensional", networkBoard.algorithmBoard.tridimensional], ["Algorithm Type", networkBoard.algorithmBoard.algorithmType],
                ["Shape", networkBoard.algorithmBoard.shape]]
        }];

        let cardContainer = document.getElementById("card-container");
        cardContainer.innerText = '';

        summaries.forEach(summary => {
            const summaryCard = new SummaryCard(summary.title, summary.summary);
            cardContainer.appendChild(summaryCard);
        });
    }
}

async function extractPlotBoard(networkBoard) {
    const mseError = document.querySelector('plotly-mse-errors');
    const scatterEP = document.querySelector('plotly-scatter-expected-predicted');
    const mseErrorLast = document.querySelector('plotly-mse-error-last');
    const neuralShape = document.querySelector('plotly-neural-shape');

    neuralShape.update(networkBoard);

    if(predicitonOboeIsOnGoing && previousOboeNetworkId === networkBoard.networkId) {
        console.log("Not refreshing plot");
        return;
    } else if(predicitonOboeIsOnGoing && !previousOboeNetworkId === networkBoard.networkId) {
        await predicitionOboe.abort();
        console.log("Stopping because new refreshing plot");
        predicitonOboeIsOnGoing = false;
        previousOboeNetworkId = networkBoard.networkId;
    } else if(previousOboeNetworkId === undefined || !previousOboeNetworkId === networkBoard.networkId) {
        console.log("Reassigning network id");
        previousOboeNetworkId = networkBoard.networkId;
    }

    console.log("Refreshing plot");
    mseError.prepare();
    //scatterEP.prepare();
    //mseErrorLast.prepare();
    predicitionOboe = apiClient.getPredictions(networkBoard.networkId, null, true,() => {
        predicitonOboeIsOnGoing = true;
    }, singlePrediction => {
        mseError.update(singlePrediction);
        //scatterEP.update(singlePrediction);
        //mseErrorLast.update(singlePrediction);
    }, () => {
        predicitonOboeIsOnGoing = false;
    });
}