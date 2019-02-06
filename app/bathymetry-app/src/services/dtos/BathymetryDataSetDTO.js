export default class BathymetryDataSetDTO {
    constructor(epsgCode, name, measurementDate, dataOwner) {
        this.epsgCode = epsgCode;
        this.name = name;
        this.measurementDate = measurementDate;
        this.dataOwner = dataOwner;
    }
}