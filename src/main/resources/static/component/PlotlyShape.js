export class PlotlyShape extends HTMLElement {

    shapeFigure;

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
    }

    async update(networkBoard) {
        this.render(networkBoard)
    }

    render(networkBoard) {

        // Improved version of the condition
        if (!networkBoard.algorithmBoard.shapeFigure) {
            return;
        }

        if (!this.shapeFigure || !this.areNestedArraysEqual(this.shapeFigure, networkBoard.algorithmBoard.shapeFigure)) {
            this.shapeFigure = networkBoard.algorithmBoard.shapeFigure;
        } else {
            return;
        }

        // Render the component's HTML
        this.shadowRoot.innerHTML = `
            <style>
                @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css');
            </style>
            <div id="plotly"></div>
        `;

        const xList = [];  // Layer index (x-axis)
        const yList = [];  // Sublayer index (y-axis)
        const zList = [];  // Neuron count (z-axis)

        for (let x = 0; x < this.shapeFigure.length; x++) {
            for (let y = 0; y < this.shapeFigure[x].length; y++) {
                for (let z = 0; z < this.shapeFigure[x][y]; z++) {
                    xList.push(x);  // Layer index
                    yList.push(y);  // Sublayer index
                    zList.push(z);  // Neuron index
                }
            }
        }
        const data = [
            // Scatter points (markers)
            {
                x: xList,  // Layer index (x-axis)
                y: yList,  // Sublayer index (y-axis)
                z: zList,  // Neuron count (z-axis)
                mode: 'markers',
                type: 'scatter3d',
                marker: {
                    size: 8,
                    color: xList,  // Color by layer index
                    colorscale: 'Viridis',
                    opacity: 0.8
                }
            }
        ];


// Define layout for Plotly
        const layout = {
            title: 'Network Shape with Layer Connections',
            scene: {
                xaxis: { title: 'Layer' },
                yaxis: { title: 'Sublayer' },
                zaxis: { title: '' }
            },
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

// Render the plot in the "plotly" div
        Plotly.newPlot(this.shadowRoot.getElementById('plotly'), data, layout);
    }


    areNestedArraysEqual(arr1, arr2) {
        // Check if the outer arrays have the same length
        if (arr1.length !== arr2.length) return false;

        // Iterate over the outer array
        for (let i = 0; i < arr1.length; i++) {
            // Check if inner arrays have the same length
            if (arr1[i].length !== arr2[i].length) return false;

            // Check if inner arrays are equal
            for (let j = 0; j < arr1[i].length; j++) {
                if (arr1[i][j] !== arr2[i][j]) {
                    return false;  // If any element is not equal, return false
                }
            }
        }

        // If all checks pass, return true
        return true;
    }

    connectedCallback() {
        this.render();
    }
}