
export class SummaryCard extends HTMLElement {
    static get observedAttributes() {
        return ['title'];
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });  // Attach Shadow DOM
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'title' || name === 'data') {
            this.render();
        }
    }

    // Render method to update the Shadow DOM with the new HTML
    render() {
        // Get the title attribute or use default
        const title = this.getAttribute('title') || 'Network Magic';
        const listOfPairs = [
            ["key1", "14"],
            ["key2", "15"],
            ["key3", "16"]
        ];
        const data = this.getAttribute('data') || listOfPairs;
        let listItems = '';
        listOfPairs.forEach(pair => {
            listItems += `
        <li class="list-group-item d-flex justify-content-between align-items-start">
            <div class="ms-2 me-auto">
                <div class="fw-bold">${pair[0]}</div>
            </div>
            <span class="badge text-bg-primary rounded-pill text-wrap">${pair[1]}</span>
        </li>
    `;
        });

        // Render the component's HTML
        this.shadowRoot.innerHTML = `
            <style>
                @import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css');
            </style>
            <div class="card" style="width: 18rem;">
                <div class="card-header">
                    ${title}  <!-- Dynamic Title -->
                </div>
                <ul class="list-group list-group-flush">
                    ${listItems}
                </ul>
            </div>
        `;
    }

    connectedCallback() {
        this.render();
    }
}
