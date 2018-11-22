import Cookies from 'universal-cookie';
import axios from 'axios';
import ServiceMeta from './ServiceMeta';

export default class UserService {
    constructor() {
        this.serviceMeta = new ServiceMeta();

        this.tokenEndpoint = this.serviceMeta.getBackendServiceAddress() + "oauth/token";
        this.activeUserEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/user/logged";
        this.logoutEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/user/logout";
        this.registerEndpoint = this.serviceMeta.getBackendServiceAddress() + "api/user/register";
        this.cookie = new Cookies();
    }

    async loginUser(username, password) {
        let formData = new FormData();
        formData.set('username', username);
        formData.set('password', password);
        formData.set('grant_type', 'password');

        let loginConfig = {
            headers:{
                'Authorization': "Basic " + btoa("bathymetry:bathymetry")
              }
        };

        return axios.post(this.tokenEndpoint, formData, loginConfig)
        .then(response => {
            let accessTokenExpireDate = new Date();
            accessTokenExpireDate.setTime(accessTokenExpireDate.getTime() + 60*60*1000)
            this.cookie.set("access_token", response.data.access_token, {path: '/', expires: accessTokenExpireDate});
            
            let refreshTokenExpireDate = new Date();
            refreshTokenExpireDate.setTime(refreshTokenExpireDate.getDate + 24*60*60*1000);
            this.cookie.set("refresh_token", response.data.refresh_token, {path: '/', expires: refreshTokenExpireDate});



        })
    }

    getConfig() {
        let config = {
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            }
        };
        return config;
    }

    async logoutUser() {

        return axios.delete(this.logoutEndpoint, this.getConfig())
        .then(response => {
            this.cookie.remove("access_token", {path: '/'});
            this.cookie.remove("refresh_token", {path: '/'});
        })
    }

    async getUser() {
        return axios.get(this.activeUserEndpoint, this.getConfig());
    }
}