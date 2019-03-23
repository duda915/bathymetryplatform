import { BathymetryMap } from "./BathymetryMap";
import TileWMS from "ol/source/TileWMS.js";
import { geoServerAPI } from "../../../services/ServiceMetaData";
import { handleRequest } from "../../utility/requesthandler";
import API from "../../../services/API";
import { transformExtent } from "ol/proj";
import CoordinateDTO from "../../../services/dtos/CoordinateDTO";
import BoundingBoxDTO from "../../../services/dtos/BoundingBoxDTO";

export default class ConnectedBathymetryMap {
  constructor(bathymetryLayers) {
    this._map = new BathymetryMap();
    this._style = "primarystyle";
    this._layers = bathymetryLayers;
    this._layersInitializer();
  }

  _layersInitializer() {
    if (this._layers.length !== 0) {
      this._initBathymetryLayers();
      this._initOnClickFunction();
    }
  }

  _initBathymetryLayers = () => {
    this._layers.forEach(layer => {
      if (layer.visible) {
        const wmsSource = this._buildWmsSource(`bathymetry:${layer.id}`);
        this._map.addLayer(layer.id, wmsSource);
      }
    });
  };

  _initOnClickFunction = () => {
    let layersParam = "";
    this._layers.forEach(layer => {
      layersParam += `bathymetry:${layer.id},`;
    });
    layersParam = layersParam.substring(0, layersParam.length - 1);

    const wmsSource = this._buildWmsSource(layersParam);

    const api = new API();

    const onClickFun = evt => {
      const resolution = this._map.getView().getResolution();

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

  _initDragBoxInteraction = () => {
    const dragBoxFunction = dragBox => {
      const dragBoxExtent = dragBox.getGeometry().getExtent();
      const transformedExtent = transformExtent(
        dragBoxExtent,
        "EPSG:3857",
        "EPSG:4326"
      );

      const upperLeft = new CoordinateDTO(
        transformedExtent[0],
        transformedExtent[3]
      );
      const lowerRight = new CoordinateDTO(
        transformedExtent[2],
        transformedExtent[1]
      );
      const box = new BoundingBoxDTO(upperLeft, lowerRight);
    };
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
}
