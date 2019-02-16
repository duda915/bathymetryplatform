export const restAPI = "http://" + window.location.hostname + ":8082/";
export const geoServerAPI =
  "http://" + window.location.hostname + ":8081/geoserver/bathymetry/wms";
export const restBasicToken = "Basic " + btoa("bathymetry:bathymetry");

export const endpoints = {
  token: `${restAPI}oauth/token`,
  data: `${restAPI}api/data`,
  userData: `${this.data}/user`,
  downloadData: `${this.data}/download`,
  downloadSelectedData: `${this.downloadData}/selection`,
  countSelection: `${this.downloadSelectionData}/count`,
  boundingBox: `${this.data}/box`,
  user: `${restAPI}api/user`
};
