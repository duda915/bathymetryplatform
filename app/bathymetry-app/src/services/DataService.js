import axios from 'axios';
import ServiceMeta from './ServiceMeta';
import Cookies from 'universal-cookie';
import UserService from './UserService';
import downloadjs from 'downloadjs';

export default class DataService {
    constructor() {
        this.serviceMeta = new ServiceMeta();

        this.dataSetsEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/datasets";;
        this.addDataEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/add";
        this.downloadDataEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/data/getdata";

        this.cookie = new Cookies();
        this.userService = new UserService();
    }

    async getDataSets() {
        return axios.get(this.dataSetsEndpoint, this.userService.getConfig())
    }

    async downloadDataSet(id) {
        return axios.get(this.downloadDataEndpoint, this.userService.getConfig())
        .then(response => {
            downloadjs(response.data, "bathymetry"+id+".csv", "text/plain");
        })
    }

    async addData(params, file) {
        let url = new URL(this.addDataEndpoint);
        url.search = new URLSearchParams(params);
        let formData = new FormData();
        formData.append("file", file);

        return axios.post(url, formData, this.userService.getConfig());
    }
}