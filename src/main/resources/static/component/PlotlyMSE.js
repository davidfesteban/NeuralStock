export class PlotlyMSE extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });  // Attach Shadow DOM
        this.mseDataMap = new Map();
    }

    prepare(override) {
        if (override) {
            this.mseErrorElement = null;
            this.mseDataMap = new Map();
        }
    }

    async update(singleMSEData) {
        if(this.mseDataMap.has(singleMSEData.epochHappened) && this.mseDataMap.get(singleMSEData.epochHappened) === singleMSEData.error) {
            return;
        }

        this.mseDataMap.set(singleMSEData.epochHappened, singleMSEData.error);
        this.render(singleMSEData);
    }

    // Render method to update the Shadow DOM with the new HTML
    render(singleMSEData) {
        if (!this.mseErrorElement) {
            // Initialize the chart on the first render
            this.shadowRoot.innerHTML = `
            <style>
                @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css');
            </style>
            <div id="plotly"></div>
        `;
            this.mseErrorElement = this.shadowRoot.getElementById('plotly');

            // Prepare initial data and layout for Plotly
            const mseData = [{
                x: [singleMSEData.epochHappened],
                y: [singleMSEData.error],
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

            // Create the initial plot
            Plotly.react(this.mseErrorElement, mseData, mseLayout);
        } else {
            // Prepare new data to be added
            const update = {
                x: [[singleMSEData.epochHappened]],  // New x value (epoch)
                y: [[singleMSEData.error]]  // New y value (MSE error)
            };

            // Extend the existing trace with new data points
            Plotly.extendTraces(this.mseErrorElement, update, [0]);  // Trace index 0 (single trace)
        }
    }


    connectedCallback() {
        this.render();
    }
}