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
let networkBoards;

//Window Register
window.onload = function () {
    apiClient.getAllNetworksSSE(networkBoardList => {
        console.log("Received event: ", networkBoardList);

        networkBoards = networkBoardList;
        const dropdown = document.getElementById("network-uuid-list");
        dropdown.innerHTML = '';

        networkBoards.forEach(networkBoard => {
            const li = document.createElement('li');
            const a = document.createElement('a');

            a.classList.add('dropdown-item');
            a.href = '#';
            a.textContent = networkBoard.networkId;

            if (currentBoardId === networkBoard.networkId) {
                extractCardsDOM(networkBoard);
                extractPlotBoard(networkBoard);
            }

            a.addEventListener('click', () => {
                currentBoardId = networkBoard.networkId;
                extractCardsDOM(networkBoard);
                extractPlotBoard(networkBoard);
            });

            li.appendChild(a);
            dropdown.appendChild(li);
        });
    })
};

function extractCardsDOM(networkBoard) {
    let summaries = [{
        title: "Network Status",
        summary: [["Network Id", networkBoard.networkId], ["Total Epochs", networkBoard.status.accumulatedEpochs],
            ["DataSet Size", networkBoard.datasetSize], ["Predictions Size", networkBoard.predictionsSize],
            ["Input Size", networkBoard.inputSize], ["Output Size", networkBoard.outputSize]]
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
        const summaryCard = document.createElement('summary-card');
        summaryCard.setAttribute('title', summary.title);
        summaryCard.setAttribute('data', summary.summary);

        // Append the summary card to the container
        cardContainer.appendChild(summaryCard);
    });

}

async function extractPlotBoard(networBoard) {
    const mseError = document.querySelector('plotly-mse-errors');
    const scatterEP = document.querySelector('plotly-scatter-expected-predicted');
    const mseErrorLast = document.querySelector('plotly-mse-error-last');
    const neuralShape = document.querySelector('plotly-neural-shape');

    neuralShape.update(networBoard);

    mseError.update(networBoard)
        .then(plotBoard => {
          scatterEP.update(plotBoard);
          mseErrorLast.update(plotBoard);
        })

}