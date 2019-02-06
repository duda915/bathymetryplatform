import Cookies from 'universal-cookie';
import axios from 'axios';
import ServiceMeta from './ServiceMeta';

export default class UserService {
    constructor() {
        this.serviceMeta = new ServiceMeta();

        this.backend = this.serviceMeta.getBackendServiceAddress();

        this.tokenEndpoint = this.backend + "oauth/token";
        this.userEndpoint = this.backend + "api/user";
        this.cookie = new Cookies();
    }

    async loginUser(username, password) {
        let formData = new FormData();
        formData.set('username', username);
        formData.set('password', password);
        formData.set('grant_type', 'password');

        let loginConfig = {
            headers: {
                'Authorization': this.serviceMeta.getBasicAuthorizationHeader()
            }
        };

        return axios.post(this.tokenEndpoint, formData, loginConfig)
            .then(response => this.saveTokens(response));
    }

    async changePassword(passwordDTO) {
        return axios.put(this.userEndpoint, passwordDTO, this.getTokenAuthorizationHeaderConfig());
    }

    logoutUser() {
        this.cookie.remove("access_token", { path: '/' });
        this.cookie.remove("refresh_token", { path: '/' });
    }

    async getUser() {
        return axios.get(this.userEndpoint, this.getTokenAuthorizationHeaderConfig())
    }

    saveTokens(tokenResponse) {
        let accessTokenExpireDate = new Date();
        accessTokenExpireDate.setTime(accessTokenExpireDate.getTime() + 60 * 60 * 1000)
        this.cookie.set("access_token", tokenResponse.data.access_token, { path: '/', expires: accessTokenExpireDate });

        let refreshTokenExpireDate = new Date();
        refreshTokenExpireDate.setTime(refreshTokenExpireDate.getDate + 24 * 60 * 60 * 1000);
        this.cookie.set("refresh_token", tokenResponse.data.refresh_token, { path: '/', expires: refreshTokenExpireDate });
    }

    getTokenAuthorizationHeaderConfig() {
        let config = {
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            }
        };
        return config;
    }


}