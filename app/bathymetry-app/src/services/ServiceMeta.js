
export default class ServiceMeta {
    
    getBackendServiceAddress() {
        const host = "http://localhost:8080/";
        return host;
    }

    getGeoServerServiceAddress() {
        const geoserver = "http://localhost:8081/geoserver/bathymetry/wms?";
        return geoserver;
    }
}
