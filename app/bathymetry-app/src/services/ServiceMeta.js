
export default class ServiceMeta {
    
    getBackendServiceAddress() {
        const host = "http://51.38.132.245:8082/";
        return host;
    }

    getGeoServerServiceAddress() {
        const geoserver = "http://51.38.132.245:8081/geoserver/bathymetry/wms?";
        return geoserver;
    }
}