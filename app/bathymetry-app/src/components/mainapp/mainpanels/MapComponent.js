import { platformModifierKeyOnly } from "ol/events/condition.js";
import { boundingExtent } from "ol/extent.js";
import { DragBox } from "ol/interaction.js";
import LayerGroup from "ol/layer/Group";
import { default as LayerTile, default as TileLayer } from "ol/layer/Tile";
import Map from "ol/Map";
import { transform, transformExtent } from "ol/proj.js";
import SourceOSM from "ol/source/OSM";
import TileWMS from "ol/source/TileWMS.js";
import View from "ol/View";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import React, { Component } from "react";
import DataService from "../../../services/DataService";
import BoundingBoxDTO from "../../../services/dtos/BoundingBoxDTO";
import CoordinateDTO from "../../../services/dtos/CoordinateDTO";
import GeoServerService from "../../../services/GeoServerService";
import ServiceMeta from "../../../services/ServiceMeta";

export default class MapComponent extends Component {
  constructor(props) {
    super(props);

    this.map = null;
    this.layer = null;
    this.wmsSource = null;
    this.olView = null;

    this.selectedLayersGroup = null;

    this.olGenerateGetFeatureInfoFunction = this.olGenerateGetFeatureInfoFunction.bind(
      this
    );
    this.hideDialog = this.hideDialog.bind(this);
    this.downloadAccept = this.downloadAccept.bind(this);

    this.serviceMeta = new ServiceMeta();
    this.dataService = new DataService();
    this.geoServerService = new GeoServerService();

    this.state = {
      downloadDialog: false,
      selectionRecords: 0
    };
  }

  componentDidMount() {
    const layersIds = this.getCol(this.props.layers, "id");
    this.initOpenLayers();
    if (layersIds.length !== 0) {
      this.loadLayers(layersIds);
      this.setMapOnClickFunction(layersIds);
    }
  }

  getCol(array, col) {
    return array.map(val => val[col]);
  }

  initOpenLayers() {
    this.olView = new View({
      projection: "EPSG:3857",
      center: [19, 51],
      zoom: 2
    });

    this.selectedLayersGroup = new LayerGroup({
      title: "Selected layers",
      layers: []
    });

    this.map = new Map({
      target: "map",
      layers: [
        new LayerGroup({
          title: "Base maps",
          layers: [
            new LayerTile({
              title: "OSM",
              type: "base",
              visible: true,
              source: new SourceOSM()
            })
          ]
        }),
        this.props.layersGroup
      ],
      view: this.olView
    });

    this.addDragBoxInteractionToMap();
  }

  addDragBoxInteractionToMap() {
    let dragBox = new DragBox({
      condition: platformModifierKeyOnly
    });
    dragBox.on(
      "boxend",
      function() {
        let extent = dragBox.getGeometry().getExtent();
        let transformed = transformExtent(extent, "EPSG:3857", "EPSG:4326");
        const upperLeftCoord = new CoordinateDTO(
          transformed[0],
          transformed[3]
        );
        const lowerLeftCoord = new CoordinateDTO(
          transformed[2],
          transformed[1]
        );
        const box = new BoundingBoxDTO(upperLeftCoord, lowerLeftCoord);
        this.setState({ box: box });
        if (this.props.layers.length === 0) {
          return;
        }

        const layersIds = this.getCol(this.props.layers, "id");

        this.props.loadingService(true);
        this.dataService
          .getSelectionDataSetCount(layersIds, box)
          .then(response => {
            if (response.data.response === "0") {
              this.props.messageService(
                "info",
                "No data",
                "No data found within polygon"
              );
            } else {
              this.setState(
                { selectionRecords: response.data.response },
                callback => {
                  this.setState({ downloadDialog: true });
                }
              );
            }
          })
          .finally(e => this.props.loadingService(false));
      }.bind(this)
    );
    this.map.addInteraction(dragBox);
  }

  updateLayerGroup() {
    this.map.getLayers().pop();
    this.map.getLayers().push(this.props.layersGroup);
    this.map.render();
  }

  downloadAccept() {
    const layersIds = this.getCol(this.props.layers, "id");

    this.props.loadingService(true);
    this.setState({ downloadDialog: false, selectionRecords: 0 });
    this.dataService
      .downloadSelectedDataSets(layersIds, this.state.box)
      .finally(e => this.props.loadingService(false));
  }

  hideDialog() {
    this.setState({ downloadDialog: false, selectionRecords: 0 });
  }

  loadLayers(layers) {
    if (layers.length === 0) {
      return;
    }
    layers.forEach(layer => this.loadLayer(layer));
  }

  setMapOnClickFunction(layers) {
    let layersParam = "bathymetry:" + layers[0];

    for (let i = 1; i < layers.length; i++) {
      layersParam += ",bathymetry:" + layers[i];
    }

    let wmsParams = {
      LAYERS: layersParam,
      TILED: true
    };

    this.wmsSource = new TileWMS({
      url: this.serviceMeta.getGeoServerAddress(),
      params: wmsParams,
      serverType: "geoserver",
      transition: 0,
      projection: "EPSG:3857"
    });

    this.dataService.getLayerBoundingBox(layers[0]).then(response => {
      const upperLeft = response.data.upperLeftVertex;
      const lowerRight = response.data.lowerRightVertex;
      const xCenter = (upperLeft.x + lowerRight.x) / 2;
      const yCenter = (upperLeft.y + lowerRight.y) / 2;

      const coord = [xCenter, yCenter];
      const reprojected = transform(coord, "EPSG:4326", "EPSG:3857");

      this.olView = new View({
        projection: "EPSG:3857",
        center: reprojected,
        zoom: 10
      });

      this.map.setView(this.olView);

      const coordExtentUL = [upperLeft.x, upperLeft.y];
      const coordExtentLR = [lowerRight.x, lowerRight.y];

      const reprojectedUL = transform(coordExtentUL, "EPSG:4326", "EPSG:3857");
      const reprojectedLR = transform(coordExtentLR, "EPSG:4326", "EPSG:3857");

      const ext = new boundingExtent([reprojectedUL, reprojectedLR]);
      this.map.getView().fit(ext);

      const olOnClickFunction = this.olGenerateGetFeatureInfoFunction;
      this.map.on("singleclick", olOnClickFunction);
    });
  }

  zoomToLayer(layer) {
    this.dataService.getLayerBoundingBox(layer).then(response => {
      const upperLeft = response.data.upperLeftVertex;
      const lowerRight = response.data.lowerRightVertex;
      const coordExtentUL = [upperLeft.x, upperLeft.y];
      const coordExtentLR = [lowerRight.x, lowerRight.y];

      const reprojectedUL = transform(coordExtentUL, "EPSG:4326", "EPSG:3857");
      const reprojectedLR = transform(coordExtentLR, "EPSG:4326", "EPSG:3857");

      const ext = new boundingExtent([reprojectedUL, reprojectedLR]);
      this.map.getView().fit(ext);
    });
  }

  loadLayer(layer) {
    const wmsParams = {
      LAYERS: `bathymetry:${layer}`,
      TILED: true,
      STYLES: this.props.layerStyle
    };

    const wmsSource = new TileWMS({
      url: this.serviceMeta.getGeoServerAddress(),
      params: wmsParams,
      serverType: "geoserver",
      transition: 0,
      projection: "EPSG:3857"
    });

    this.layer = new TileLayer({
      title: layer,
      source: wmsSource
    });

    this.selectedLayersGroup.getLayers().push(this.layer);
  }

  olGenerateGetFeatureInfoFunction(evt) {
    let viewResolution = this.olView.getResolution();
    let url = this.wmsSource.getGetFeatureInfoUrl(
      evt.coordinate,
      viewResolution,
      "EPSG:3857",
      { INFO_FORMAT: "application/json" }
    );
    if (url) {
      this.geoServerService.geoserverGetFeatureInfo(url).then(response => {
        let features = response.data.features;

        if (features.length === 0) {
          return;
        }

        let info = "measurement: " + features[0].properties.GRAY_INDEX;
        this.props.messageService("info", "Point", info);
      });
    }
  }

  updateMapSize() {
    this.map.updateSize();
  }

  updateLayers() {
    const layersIds = this.getCol(this.props.layers, "id");
    this.selectedLayersGroup.getLayers().clear();
    this.loadLayers(layersIds);
  }

  render() {
    const dialogFooter = (
      <div>
        <Button
          label="Download"
          icon="pi pi-check"
          onClick={this.downloadAccept}
        />
        <Button label="Cancel" icon="pi pi-times" onClick={this.hideDialog} />
      </div>
    );

    return (
      <div className="mapComponent" style={{ height: "100%" }}>
        <Dialog
          header="Download selection"
          footer={dialogFooter}
          visible={this.state.downloadDialog}
          width="350px"
          modal={true}
          onHide={this.hideDialog}
        >
          Found {this.state.selectionRecords} records.
        </Dialog>
        <div id="map" style={{ height: "100%" }} />
      </div>
    );
  }
}
