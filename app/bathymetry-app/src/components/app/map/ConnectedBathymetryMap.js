import { BathymetryMap } from "./BathymetryMap";
import TileWMS from "ol/source/TileWMS.js";
import { geoServerAPI } from "../../../services/ServiceMetaData";
import { handleRequest } from "../../utility/requesthandler";
import API from "../../../services/API";
import { transform, transformExtent } from "ol/proj";
import CoordinateDTO from "../../../services/dtos/CoordinateDTO";
import BoundingBoxDTO from "../../../services/dtos/BoundingBoxDTO";
import { sendMapCommand } from "./MapActions";
import { store } from "../../store";
import { boundingExtent, containsExtent } from "ol/extent";
import { Commands } from "./MapCommands";

export default class ConnectedBathymetryMap {
  constructor(bathymetryLayers, style) {
    this._map = new BathymetryMap();
    this._style = style;
    this._layers = bathymetryLayers;

    this._regressionExtent = null;
    this._onClickInteraction = null;
    this._onDragBoxInteraction = null;

    this._initializeLayers();
    this.zoomToFit();
  }

  _initializeLayers() {
    this._initBathymetryLayers();
    this._initOnClickInteraction();
    this._initDragBoxInteraction();
  }

  setLayers = layers => {
    this._layers = layers;
    this._initializeLayers();
  };

  setStyle = style => {
    this._style = style;
    this._initializeLayers();
  };

  zoomToLayer = layerId => {
    const api = new API();
    handleRequest({
      requestPromise: api.restData().getLayerBoundingBox(layerId),
      onSuccess: response => {
        const extent = this._boxToExtentWithTransform(response.data);
        this._map.zoomToExtent(extent);
      }
    });
  };

  zoomToFit = () => {
    if (this.getVisibleLayers().length === 0) {
      return;
    }

    const api = new API();
    const layerIds = this.getVisibleLayers().map(layer => layer.id);

    handleRequest({
      requestPromise: api.restData().getActiveLayersBoundingBox(layerIds),
      onSuccess: response => {
        const extent = this._boxToExtentWithTransform(response.data);
        this._map.zoomToExtent(extent);
      }
    });
  };

  getVisibleLayers = () => {
    return this._layers.filter(layer => layer.visible);
  };

  turnOnRegressionService = () => {
    const api = new API();

    handleRequest({
      requestPromise: api.restData().getRegressionBounds(),
      onSuccess: response => {
        this._regressionExtent = this._boxToExtentWithTransform(response.data);
        this._map.drawPolygon(this._regressionExtent);
        this._map.zoomToExtent(this._regressionExtent);
      },
      onSuccessMessage: () =>
        "select area with ctrl inside bounds to calculate bathymetry with neural network"
    });
  };

  checkIfBoxContainsInRegressioExtent = box => {
    const extent = this._boxToExtentWithTransform(box);
    return containsExtent(this._regressionExtent, extent);
  };

  getRegressionExtent = () => {
    console.log(this._regressionExtent);
    return this._regressionExtent;
  };

  _boxToExtentWithTransform({ upperLeftVertex, lowerRightVertex }) {
    const coordsUL = [upperLeftVertex.x, upperLeftVertex.y];
    const coordsLR = [lowerRightVertex.x, lowerRightVertex.y];
    const extent = new boundingExtent([coordsUL, coordsLR]);
    const transformedExtent = transformExtent(extent, "EPSG:4326", "EPSG:3857");
    return transformedExtent;
  }

  _initBathymetryLayers = () => {
    this._map.removeLayers();

    this.getVisibleLayers().forEach(layer => {
      const wmsSource = this._buildWmsSource(`bathymetry:${layer.id}`);
      this._map.addLayer(layer.id, wmsSource);
    });
  };

  _initOnClickInteraction = () => {
    this._map.removeOnClickInteraction(this._onClickInteraction);
    this._onClickInteraction = this._getOnClickFunction();
    this._map.addOnClickInteraction(this._onClickInteraction);
  };

  _getOnClickFunction = () => {
    const layersParam = this._getCombinedLayersParam();
    const wmsSource = this._buildWmsSourceWithoutStyle(layersParam);

    const onClickFun = evt => {
      const resolution = this._map.getView().getResolution();

      const url = wmsSource.getGetFeatureInfoUrl(
        evt.coordinate,
        resolution,
        "EPSG:3857",
        { INFO_FORMAT: "application/json" }
      );

      const coordinate = transform(evt.coordinate, "EPSG:3857", "EPSG:4326");

      store.dispatch(
        sendMapCommand({
          commandType: Commands.FETCH_FEATURE_INFO,
          commandPayload: {
            url,
            coordinate
          }
        })
      );
    };

    return onClickFun;
  };

  _getCombinedLayersParam = () => {
    let layersParam = "";
    this.getVisibleLayers().forEach(layer => {
      layersParam += `bathymetry:${layer.id},`;
    });
    layersParam = layersParam.substring(0, layersParam.length - 1);
    return layersParam;
  };

  _initDragBoxInteraction = () => {
    const dragBoxInteractionFunction = dragBox => {
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

      store.dispatch(
        sendMapCommand({
          commandType: Commands.HANDLE_DRAG_BOX,
          commandPayload: box
        })
      );
    };

    this._map.addDragBoxInteraction(dragBoxInteractionFunction);
  };

  //wms params with style not working with feature info
  _buildWmsSourceWithoutStyle = layersParam => {
    const wmsParams = {
      LAYERS: layersParam,
      TILED: true
    };

    return new TileWMS({
      url: geoServerAPI,
      params: wmsParams,
      serverType: "geoserver",
      transition: 0,
      projection: "EPSG:3857"
    });
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
