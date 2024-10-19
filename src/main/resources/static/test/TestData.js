import {apiClient} from "../global.js";
import {AlgorithmBoard} from "../classes/AlgorithmBoard.js";
import {DataPair} from "../classes/DataPair.js";

//TODO: Complexity, and enum Types in JS
export class TestData {
    networksUUID = [];

    createDatasetSum(networkId) {
        const datapairs = [];

        for (let x = 0; x < 5; x++) {
            for (let y = 0; y < 5; y++) {
                for (let z = 0; z < 5; z++) {
                    const input = [x, y, z];  // List of inputs (x and y)
                    const output = [x + y + z];  // Output is the sum of x and y
                    const datapair = new DataPair(
                        crypto.randomUUID(),  // Generate random UUID (you can use any UUID generation method)
                        Date.now(),  // Use current timestamp in milliseconds
                        networkId,
                        input,  // Inputs list
                        output  // Outputs list
                );
                    datapairs.push(datapair);  // Add the datapair to the list
                }
            }
        }

        return datapairs;  // Return the list of datapairs
    }

    createDataset() {
        const dataset = [];

        // Loop through numbers from 0 to 10
        for (let a = 0; a <= 10; a++) {
            for (let b = 0; b <= 10; b++) {
                // Perform sum (operation 0)
                dataset.push(new DataPair(
                    crypto.randomUUID(),  // Generate a random UUID for each DataPair
                    "SumRestMultiply",
                    Date.now(),  // Use the current timestamp
                    [a, b, 0],  // Inputs list [a, b, operation (0 = sum)]
                    [a + b]  // Expected result as a list
                ));

                // Perform subtraction (operation 1)
                dataset.push(new DataPair(
                    crypto.randomUUID(),
                    "SumRestMultiply",
                    Date.now(),
                    [a, b, 1],  // Inputs list [a, b, operation (1 = subtraction)]
                    [a - b]  // Expected result as a list
                ));

                // Perform multiplication (operation 2)
                dataset.push(new DataPair(
                    crypto.randomUUID(),
                    "SumRestMultiply",
                    Date.now(),
                    [a, b, 2],  // Inputs list [a, b, operation (2 = multiplication)]
                    [a * b]  // Expected result as a list
                ));
            }
        }

        return dataset;
    }

    async createTestData() {

        await apiClient.dataSetAPI.includeDataSet(this.createDataset(), () => {});

        await apiClient.networkAPI.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1.5,
            true, "LEAKY_RELU", "PERCEPTRON", null), uuid => {
            this.networksUUID.push(uuid.uuid)
        });

        await apiClient.networkAPI.createNetwork(new AlgorithmBoard(3, 1, 0.0001, 1.5,
            true, "LEAKY_RELU", "PERCEPTRON", null), uuid => {
            this.networksUUID.push(uuid.uuid)
        });

        await apiClient.networkAPI.createNetwork(new AlgorithmBoard(3, 1, 0.0001, 1.5,
            true, "LEAKY_RELU", "TRIANGLE", null), uuid => {
            this.networksUUID.push(uuid.uuid)
        });

        /*
        await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1.5,
            true, "LEAKY_RELU", "COMPRESSOR", null), uuid => {
            this.networksUUID.push(uuid.uuid)
            apiClient.includeDataSet(uuid.uuid, this.createDataset(uuid.uuid));
        })


        await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1.5,
            false, "LEAKY_RELU", "EXPANDER", null), uuid => {
            this.networksUUID.push(uuid.uuid)
            apiClient.includeDataSet(uuid.uuid, this.createDataset(uuid.uuid));
        })

        await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1.5,
            true, "LEAKY_RELU", "EXPANDER", null), uuid => {
            this.networksUUID.push(uuid.uuid)
            apiClient.includeDataSet(uuid.uuid, this.createDataset(uuid.uuid));
        })
*/
        //await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1,
        //    true, "LEAKY_RELU", "PERCEPTRON", null), uuid => {
        //    this.networksUUID.push(uuid.uuid)
        //    apiClient.includeDataSet(uuid.uuid, this.createDatasetSum(uuid.uuid));
        //})
        //await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1.5,
        //    true, "LEAKY_RELU", "PERCEPTRON", null), uuid => {
        //    this.networksUUID.push(uuid.uuid)
        //    apiClient.includeDataSet(uuid.uuid, this.createDatasetSum(uuid.uuid));
        //})
    }

    async compute(epoch) {
        this.networksUUID.forEach(uuid => apiClient.predictionAPI.compute("SumRestMultiply", uuid, epoch));
    }

}

// Make it globally accessible
window.TestData = new TestData();