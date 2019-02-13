import axios from 'axios';
import ServiceMeta from './ServiceMeta';
import Cookies from 'universal-cookie';
import UserService from './UserService';
import downloadjs from 'downloadjs';

export default class DataService {
    constructor() {
        this.serviceMeta = new ServiceMeta();

        this.backend = this.serviceMeta.getBackendServiceAddress();

        this.dataEndpoint = this.backend + "api/data";
        this.userDataEndpoint = this.dataEndpoint + "/user";

        this.downloadDataEndpoint = this.dataEndpoint + "/download";
        this.downloadSelectionEndpoint = this.downloadDataEndpoint + "/selection";
        this.downloadSelectionCountEndpoint = this.downloadSelectionEndpoint + "/count";
        this.layerCenterEndpoint = this.dataEndpoint + "/center";
        this.layerBoundingBoxEndpoint = this.dataEndpoint + "/box";

        this.cookie = new Cookies();
        this.userService = new UserService();
    }

    async getDataSets() {
        return axios.get(this.dataEndpoint, this.userService.getTokenAuthorizationHeaderConfig())
    }

    async getUserDataSets() {
        return axios.get(this.userDataEndpoint, this.userService.getTokenAuthorizationHeaderConfig());
    }

    async downloadDataSet(id) {
        let params = {
            id: id
        };

        let url = new URL(this.downloadDataEndpoint);
        url.search = new URLSearchParams(params);

        return axios.get(url, this.userService.getTokenAuthorizationHeaderConfig())
            .then(response => {
                downloadjs(response.data, "bathymetry" + id + ".csv", "text/plain");
            })
    }

    async downloadSelectedDataSets(ids, boundingBoxDTO) {
        let url = new URL(this.downloadSelectionEndpoint);
        const urlParams = new URLSearchParams();

        ids.forEach(element => {
            urlParams.append("id", element);
        });

        url.search = urlParams;

        return axios.post(url, boundingBoxDTO, this.userService.getTokenAuthorizationHeaderConfig())
            .then(response => {
                downloadjs(response.data, "bathymetry_selection.csv", "text/plain");
            });
    }

    async getSelectionDataSetCount(ids, boundingBoxDTO) {
        let url = new URL(this.downloadSelectionCountEndpoint);
        const urlParams = new URLSearchParams();

        ids.forEach(element => {
            urlParams.append("id", element);
        });

        url.search = urlParams;

        return axios.post(url, boundingBoxDTO, this.userService.getTokenAuthorizationHeaderConfig());
    }

    async addData(bathymetryDataSetDTO, file) {
        bathymetryDataSetDTO.measurementDate = this.formatDate(bathymetryDataSetDTO.measurementDate);
        
        let url = new URL(this.dataEndpoint);
        let formData = new FormData();
        formData.append("file", file);
        formData.append("data", new Blob([JSON.stringify(bathymetryDataSetDTO)], {type: "application/json"}));

        return axios.post(url, formData, this.userService.getTokenAuthorizationHeaderConfig());
    }

    formatDate(date) {
        var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();
    
        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;
    
        return [year, month, day].join('-');
    }

    async deleteData(id) {
        let params = {
            id: id
        };

        let url = new URL(this.dataEndpoint);
        url.search = new URLSearchParams(params);

        return axios.delete(url, this.userService.getTokenAuthorizationHeaderConfig());
    }

    async getLayerCenter(id) {
        return this.singleIdGet(id, this.layerCenterEndpoint);
    }

    async getLayerBoundingBox(id) {
        return this.singleIdGet(id, this.layerBoundingBoxEndpoint);
    }

    singleIdGet(id, endpoint) {
        let params = {
            id: id
        };
        let url = new URL(endpoint);
        url.search = new URLSearchParams(params);
        return axios.get(url, this.userService.getTokenAuthorizationHeaderConfig());
    }

    

}



