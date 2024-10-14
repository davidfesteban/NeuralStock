
export class PlotlyScatterEP extends HTMLElement {
     //Array
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });  // Attach Shadow DOM
    }

    async update(networkBoard) {

    }

    // Render method to update the Shadow DOM with the new HTML
    render(plotboard) {
        //// Get the title attribute or use default
        //const title = 'MSE Error';
//
        //// Render the component's HTML
        //this.shadowRoot.innerHTML = `
        //    <style>
        //        @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css');
        //    </style>
        //    <div class="card">
        //        <h5>
        //            ${title}
        //        <h5>
        //        <div id="plotly"></div>
        //        <table class="table table-bordered" id="prediction-table">
        //            <thead>
        //                <tr>
        //                    <th scope="col">Type</th>
        //                    <th scope="col">Values</th>
        //                </tr>
        //            </thead>
        //            <tbody>
        //                <!-- Rows will be inserted dynamically here -->
        //            </tbody>
        //        </table>
        //    </div>
        //`;
//
        //plotboard.lastEpochPredicted.inputs; // Array
        //plotboard.lastEpochPredicted.expected; //Array
        //plotboard.lastEpochPredicted.predicted;
//
//
        //const mseErrorElement = this.shadowRoot.getElementById('plotly');
        //const mseData = [{
        //    x: [...Array(plotboard.mseErrors.length).keys()], // Create an array of epochs
        //    y: plotboard.mseErrors, // The MSE error values
        //    mode: 'lines+markers',
        //    type: 'scatter',
        //    name: 'MSE Errors'
        //}];
        //const mseLayout = {
        //    title: 'MSE Errors Over Epochs',
        //    xaxis: { title: 'Epoch' },
        //    yaxis: { title: 'MSE Error' }
        //};
//
        //Plotly.newPlot(mseErrorElement, mseData, mseLayout);
    }

    connectedCallback() {
        this.render();
    }
}