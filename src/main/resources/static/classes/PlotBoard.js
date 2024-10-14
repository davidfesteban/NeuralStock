import {PredictedData} from "./PredictedData.js";

export class PlotBoard {
    constructor(lastEpochPredicted, mseErrors) {
        this.lastEpochPredicted = lastEpochPredicted;  // Array of PredictedData
        this.mseErrors = mseErrors;  // Array<number>
    }

    static fromJson(jsonObj) {
        return new PlotBoard(
            //jsonObj.lastEpochPredicted.map(item => PredictedData.fromJson(item)),
            jsonObj.mseErrors
        );
    }
}