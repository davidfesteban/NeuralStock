import {apiClient} from "../global.js";

export class PlotlyMSE extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });  // Attach Shadow DOM
    }

    async update(networkBoard) {
        let plotBoard = await apiClient.fetchPlot(networkBoard.networkId);
        this.render(plotBoard);
        return plotBoard;
    }

    // Render method to update the Shadow DOM with the new HTML
    render(plotBoard) {
        this.shadowRoot.innerHTML = `
            <style>
                @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css');
            </style>
            <div id="plotly"></div>
        `;


        const mseErrorElement = this.shadowRoot.getElementById('plotly');
        const mseData = [{
            x: [...Array(plotBoard.mseErrors.length).keys()],
            y: plotBoard.mseErrors,
            mode: 'lines+markers',
            type: 'scatter',
            name: 'MSE Errors'
        }];
        const mseLayout = {
            title: 'MSE Errors - Epochs',
            xaxis: { title: 'Epoch' },
            yaxis: { title: 'MSE Error' },
            xref: 'paper',
            autosize: true,  // Automatically resize to fill container
            margin: {
                l: 0,  // No left margin
                r: 0,  // No right margin
                t: 0,  // No top margin
                b: 0,  // No bottom margin
                pad: 0 // No padding
            }
        };

        Plotly.newPlot(mseErrorElement, mseData, mseLayout);
    }

    connectedCallback() {
        this.render();
    }
}