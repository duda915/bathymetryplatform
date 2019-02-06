import axios from 'axios';

export default class GeoServerSerivce {

    
    async geoserverGetFeatureInfo(url) {
        return axios.get(url);
    }
}