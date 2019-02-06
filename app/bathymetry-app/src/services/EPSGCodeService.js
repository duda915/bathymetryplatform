import axios from 'axios';
import ServiceMeta from './ServiceMeta';
import UserService from './UserService';

export default class EPSGCodeService {
    constructor() {
        this.serviceMeta = new ServiceMeta();
        this.backend = this.serviceMeta.getBackendServiceAddress();

        this.epsgCodesEndpoint = this.backend + "api/epsg";
        this.userService = new UserService();
    }

    async getEPSGCodes() {
        return axios.get(this.epsgCodesEndpoint, this.userService.getTokenAuthorizationHeaderConfig());
    }
}