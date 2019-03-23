import { BathymetryMap } from "./BathymetryMap";
import TileWMS from "ol/source/TileWMS.js";
import { geoServerAPI } from "../../../services/ServiceMetaData";
import { handleRequest } from "../../utility/requesthandler";
import API from "../../../services/API";

export default class ConnectedBathymetryMap {
  constructor(bathymetryLayers) {
    this._map = new BathymetryMap();
    this._style = "primarystyle";
    this._layers = this._initLayersObject(bathymetryLayers);
    this._initBathymetryLayers();
    this._initOnClickFunction();
  }

  _initBathymetryLayers = () => {
    this._layers.foreach(layer => {
      if (layer.visible) {
        const wmsSource = this._buildWmsSource(`bathymetry:${layer.id}`);
        this._map.addLayer(layer.id, wmsSource);
      }
    });
  };

  _initOnClickFunction = () => {
    const resolution = this._map.getView().getResolution();

    let layersParam = "";
    this._layers.foreach(layer => {
      layersParam += `bathymetry:${layer.id},`;
    });
    layersParam = layersParam.substring(0, layersParam.length - 1);

    const wmsSource = this._buildWmsSource(layersParam);

    const api = new API();

    const onClickFun = evt => {
      const url = wmsSource.getGetFeatureInfoUrl(
        evt.coordinate,
        resolution,
        "EPSG:3857",
        { INFO_FORMAT: "application/json" }
      );

      handleRequest({
        requestPromise: api.geoServerAPI().getFeatureInfo(url),
        onSuccessMessage: response => {
          const features = response.data.features;
          if (features.length === 0) {
            return "point lies outside of selected layers";
          }

          return `measurement: ${features[0].properties.GRAY_INDEX}`;
        }
      });
    };

    this._map.addOnClickInteraction(onClickFun);
  };

  _buildWmsSource = layersParam => {
    const wmsParams = {
      LAYERS: layersParam,
      TILED: true,
      STYLES: this._style
    };

    return new TileWMS({
      url: geoServerAPI,
      params: wmsParams,
      serverType: "geoserver",
      transition: 0,
      projection: "EPSG:3857"
    });
  };

  _initLayersObject = initLayers => {
    return initLayers.foreach(layer => (layer.visible = true));
  };
}
