import {apiClient} from "../global.js";
import {PredictedData} from "../classes/PredictedData.js";

export class PlotlyMSE extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });  // Attach Shadow DOM
        this.dataPairList = [];
    }

    prepare() {
        this.dataPairList = [];
    }

    async update(singlePrediction) {
        //const parsedPredictions = partialPredictions.map(predictionJsonObj => PredictedData.fromJson(predictionJsonObj));
        this.dataPairList.push(singlePrediction);

        // Step 1: Group by epochHappened (Map<epochHappened, List<DataPair>>)
        const groupedByEpoch = this.dataPairList.reduce((acc, item) => {
            const epoch = item.epochHappened;
            if (!acc[epoch]) {
                acc[epoch] = [];
            }
            acc[epoch].push(item);
            return acc;
        }, {});

        // Step 2: Convert Map<epochHappened, List<DataPair>> to Collection<List<DataPair>>
        const collectionOfLists = Object.values(groupedByEpoch);

        // Step 3: Transform each List<DataPair> to List<Double>
        const collectionOfDoubles = collectionOfLists.map(listOfDataPairs => {
            return listOfDataPairs.map(dataPair => {
                return dataPair.mseError;
            });
        });

        // Step 4: Compute the average of each List<Double>
        const collectionOfAverages = collectionOfDoubles.map(listOfDoubles => {
            const total = listOfDoubles.reduce((sum, value) => sum + value, 0);
            const average = total / listOfDoubles.length;
            return average;
        });

        //console.log(collectionOfAverages);

        // Call render with the collection of averages
        this.render(collectionOfAverages);
    }

    // Render method to update the Shadow DOM with the new HTML
    render(doubleList) {
        // Prepare data for the Plotly chart
        const mseData = [{
            x: [...Array(doubleList.length).keys()],
            y: doubleList,
            mode: 'lines+markers',
            type: 'scatter',
            name: 'MSE Errors'
        }];
        const mseLayout = {
            title: 'MSE Errors - Epochs',
            xaxis: { title: 'Epoch' },
            yaxis: { title: 'MSE Error' },
            xref: 'paper',
            autosize: true,
            margin: {
                l: 0,
                r: 0,
                t: 0,
                b: 0,
                pad: 0
            }
        };

        // Use Plotly.react to update the chart efficiently
        if (!this.mseErrorElement) {
            this.shadowRoot.innerHTML = `
            <style>
                @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css');
            </style>
            <div id="plotly"></div>
        `;
            this.mseErrorElement = this.shadowRoot.getElementById('plotly');
            Plotly.newPlot(this.mseErrorElement, mseData, mseLayout);
        } else {
            Plotly.react(this.mseErrorElement, mseData, mseLayout);
        }
    }

    connectedCallback() {
        this.render();
    }
}