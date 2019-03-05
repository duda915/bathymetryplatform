import { endpoints, restBasicToken } from "./ServiceMetaData";
import axios from "axios";
import Cookies from "universal-cookie";
import { registerInterceptor } from "./RefreshTokenInterceptor";

export default class API {
  constructor() {
    registerInterceptor();
  }

  restUser() {
    let rest = {};
    rest.getUser = () => axios.get(endpoints.user, this.authorization());

    rest.loginUser = (username, password) => {
      const formData = new FormData();
      formData.set("username", username);
      formData.set("password", password);
      formData.set("grant_type", "password");

      const config = {
        headers: {
          authorization: restBasicToken
        }
      };

      return axios.post(endpoints.token, formData, config);
    };

    rest.changePassword = password =>
      axios.put(endpoints.user, password, this.authorization());

    rest.registerUser = newUser => axios.post(endpoints.registerUser, newUser);

    return rest;
  }

  restData() {
    let rest = {};

    rest.getAllDataSets = () => axios.get(endpoints.data, this.authorization());

    rest.getUserDataSets = () =>
      axios.get(endpoints.userData, this.authorization());

    rest.downloadDataSet = id => {
      const url = new URL(endpoints.downloadData);
      url.search = new URLSearchParams({ id: id });

      return axios.get(url, this.authorization());
    };

    rest.countSelectedDataSets = (ids, box) => {
      const searchParams = new URLSearchParams();
      ids.forEach(element => {
        searchParams.append("id", element);
      });

      const url = new URL(endpoints.countSelection);
      url.search = searchParams;

      return axios.post(url, box, this.authorization());
    };

    rest.downloadSelectedDataSets = (ids, box) => {
      const searchParams = new URLSearchParams();
      ids.forEach(element => {
        searchParams.append("id", element);
      });

      const url = new URL(endpoints.downloadSelectedData);
      url.search = searchParams;

      return axios.post(url, box, this.authorization());
    };

    rest.uploadData = (data, file) => {
      const url = new URL(endpoints.data);
      const formData = new FormData();
      formData.append("file", file);
      formData.append(
        "data",
        new Blob([JSON.stringify(data)], { type: "application/json" })
      );

      return axios.post(url, formData, this.authorization());
    };

    rest.deleteData = id => {
      const url = new URL(endpoints.data);
      const searchParams = new URLSearchParams();
      searchParams.append("id", id);
      url.search = searchParams;

      return axios.delete(url, this.authorization());
    };

    rest.getLayerBoundingBox = id => {
      const url = new URL(endpoints.boundingBox);
      const searchParams = new URLSearchParams();
      searchParams.append("id", id);
      url.search = searchParams;

      return axios.get(url, this.authorization());
    };

    rest.getActiveLayersBoundingBox = ids => {
      const url = new URL(endpoints.multipleLayersBoundingBox);
      const searchParams = new URLSearchParams();
      ids.forEach(id => searchParams.append("ids", id));
      url.search = searchParams;

      return axios.get(url, this.authorization());
    };

    rest.getEPSGCodes = () => axios.get(endpoints.epsg, this.authorization());

    return rest;
  }

  geoServerAPI() {
    let geoServer = {};

    geoServer.getFeatureInfo = url => axios.get(url);

    return geoServer;
  }

  authorization() {
    const cookies = new Cookies();
    if (cookies.get("access_token")) {
      return {
        headers: {
          authorization: `Bearer ${cookies.get("access_token")}`
        }
      };
    }
  }
}
