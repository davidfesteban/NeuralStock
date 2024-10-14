import {apiClient} from "../global.js";
import {AlgorithmBoard} from "../classes/AlgorithmBoard.js";
import {DataPair} from "../classes/DataPair.js";

//TODO: Complexity, and enum Types in JS
export class TestData {
    static networksUUID = [];

    static createDatasetSum(networkId) {
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

    static async createTestData() {
        let networkA = await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1,
            false, "LEAKY_RELU", "PERCEPTRON", null))

        let networkB = await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1,
            true, "LEAKY_RELU", "PERCEPTRON", null))

        let networkC = await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1.5,
            false, "LEAKY_RELU", "PERCEPTRON", null))

        let networkD = await apiClient.createNetwork(new AlgorithmBoard(3, 1, 0.01, 1.5,
            true, "LEAKY_RELU", "PERCEPTRON", null))

        apiClient.includeDataSet(networkA, this.createDatasetSum(networkA));
        apiClient.includeDataSet(networkB, this.createDatasetSum(networkB));
        apiClient.includeDataSet(networkC, this.createDatasetSum(networkC));
        apiClient.includeDataSet(networkD, this.createDatasetSum(networkD));

        this.networksUUID.push(networkA, networkB, networkC, networkD);
    }

    static async compute(epoch) {
        this.networksUUID.forEach(uuid => apiClient.compute(uuid, epoch));
    }

}

// Make it globally accessible
window.TestData = TestData;