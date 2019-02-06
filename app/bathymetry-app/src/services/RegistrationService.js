
import ServiceMeta from './ServiceMeta'
import axios from 'axios';

export default class RegistrationService {
    constructor() {
        this.serviceMeta = new ServiceMeta();
        this.backend = this.serviceMeta.getBackendServiceAddress();
        this.registrationEndpoint = this.backend + "api/register";

    }

    async registerNewAccount(userDTO) {
        return axios.post(this.registrationEndpoint, userDTO);
    }


}