import axios from 'axios';
import ServiceMeta from './ServiceMeta';
import Cookies from 'universal-cookie';
import UserService from './UserService';
import downloadjs from 'downloadjs';

export default class DataService {
    constructor() {
        this.serviceMeta = new ServiceMeta();

        this.dataSetsEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/datasets";;
        this.userDataSetsEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/datasets/user";
        this.addDataEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/datasets";
        this.downloadDataEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/datasets/download";
        this.deleteUserDataEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/datasets";
        this.downloadSelectedDataSetsEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/datasets/download/geometry";

        this.cookie = new Cookies();
        this.userService = new UserService();
    }

    async getDataSets() {
        return axios.get(this.dataSetsEndpoint, this.userService.getConfig())
    }

    async downloadDataSet(id) {
        let params = {
            id: id
        };
        let url = new URL(this.downloadDataEndpoint);
        url.search = new URLSearchParams(params);

        return axios.get(url, this.userService.getConfig())
        .then(response => {
            downloadjs(response.data, "bathymetry"+id+".csv", "text/plain");
        })
    }

    async downloadSelectedDataSets(ids, polygon) {
        let url = new URL(this.downloadSelectedDataSetsEndpoint);
        url+="?";
        
        ids.forEach(element => {
            url+=("id="+element+"&");
        });

        polygon.forEach(element => {
            url+=("coords="+element+"&");
        })

        console.log(polygon);

        return axios.get(url, this.userService.getConfig());
    }

    async addData(params, file) {
        let url = new URL(this.addDataEndpoint);
        url.search = new URLSearchParams(params);
        let formData = new FormData();
        formData.append("file", file);

        console.log(url);

        return axios.post(url, formData, this.userService.getConfig());
    }

    async getUserDataSets() {
        return axios.get(this.userDataSetsEndpoint, this.userService.getConfig());
    }

    async deleteUserData(id) {
        let params = {
            id: id
        };
        let url = new URL(this.deleteUserDataEndpoint);
        url.search = new URLSearchParams(params);

        return axios.delete(url, this.userService.getConfig());
    }

    async geoserverGetFeatureInfo(url) {
        return axios.get(url);
    }
}