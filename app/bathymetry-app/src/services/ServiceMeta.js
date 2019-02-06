
export default class ServiceMeta {
    getBackendServiceAddress() {
        const host = 'http://' + window.location.hostname + ':8082/';
        return host;
    }

    getGeoServerAddress() {
        const geoserver = "http://localhost:8081/geoserver/bathymetry/wms";
        return geoserver;
    }

}
