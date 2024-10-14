
export class PlotlyMSELast extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });  // Attach Shadow DOM
    }

    async update(plotBoard) {
        this.render(plotBoard)
    }

    // Render method to update the Shadow DOM with the new HTML
    render(plotBoard) {

        // Render the component's HTML
        this.shadowRoot.innerHTML = `
            <style>
                @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css');
            </style>
            <div id="plotly"></div>
        `;

        let mseErrors = plotBoard.lastEpochPredicted.map(predictedData => predictedData.mseError)

        const plotly = this.shadowRoot.getElementById('plotly');
        const mseData = [{
            x: [...Array(mseErrors.length).keys()],
            y: mseErrors, // The MSE error values
            mode: 'lines+markers',
            type: 'scatter',
            name: 'Last Predicted MSE'
        }];
        const mseLayout = {
            title: 'Last Predicted MSE',
            xaxis: { title: 'DataPoint' },
            yaxis: { title: 'MSE' },
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

        Plotly.newPlot(plotly, mseData, mseLayout);
    }

    connectedCallback() {
        this.render();
    }
}