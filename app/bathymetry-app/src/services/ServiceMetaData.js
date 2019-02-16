export const restAPI = "http://" + window.location.hostname + ":8082/";
export const geoServerAPI =
  "http://" + window.location.hostname + ":8081/geoserver/bathymetry/wms";
export const restBasicToken = "Basic " + btoa("bathymetry:bathymetry");

export const endpoints = {
  token: `${restAPI}oauth/token`,

  data: `${restAPI}api/data`,
  userData: `${restAPI}api/data/user`,
  downloadData: `${restAPI}api/data/download`,
  downloadSelectedData: `${restAPI}api/data/selection`,
  countSelection: `${restAPI}api/data/selection/count`,
  boundingBox: `${restAPI}api/data/box`,

  user: `${restAPI}api/user`,
  registerUser: `${restAPI}api/register`,

  epsg:`${restAPI}api/epsg`,
};
