import downloadjs from "downloadjs";
import { platformModifierKeyOnly } from "ol/events/condition.js";
import { boundingExtent, containsExtent } from "ol/extent.js";
import { DragBox } from "ol/interaction.js";
import LayerGroup from "ol/layer/Group";
import * as Polygon from "ol/geom/Polygon";
import Feature from "ol/Feature";
import { default as VectorSource } from "ol/source/Vector";
import { default as VectorLayer } from "ol/layer/Vector";
import { default as LayerTile, default as TileLayer } from "ol/layer/Tile";
import Map from "ol/Map";
import { transform, transformExtent } from "ol/proj.js";
import SourceOSM from "ol/source/OSM";
import TileWMS from "ol/source/TileWMS.js";
import View from "ol/View";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import React, { Component } from "react";
import API from "../../services/API";
import BoundingBoxDTO from "../../services/dtos/BoundingBoxDTO";
import CoordinateDTO from "../../services/dtos/CoordinateDTO";
import { geoServerAPI } from "../../services/ServiceMetaData";

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

    this.api = new API();

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

        this.api
          .restData()
          .countSelectedDataSets(layersIds, box)
          .then(response => {
            if (response.data.response === "0") {
              this.props.messageService(
                "info",
                "No data",
                "no data found within polygon"
              );
            } else {
              this.setState({
                selectionRecords: response.data.response,
                downloadDialog: true
              });
            }
          })
          .catch(() =>
            this.props.messageService(
              "error",
              "Error",
              "failed to query service"
            )
          )
          .finally(() => this.props.loadingService(false));
      }.bind(this)
    );
    this.map.addInteraction(dragBox);
  }

  updateLayerGroup() {
    this.map.getLayers().pop();
    this.map.getLayers().push(this.props.layersGroup);
    this.map.render();
  }

  downloadAccept = () => {
    const layersIds = this.getCol(this.props.layers, "id");

    this.props.loadingService(true);
    this.setState({ downloadDialog: false, selectionRecords: 0 });

    this.api
      .restData()
      .downloadSelectedDataSets(layersIds, this.state.box)
      .then(response =>
        downloadjs(response.data, "bathymetry_selection.csv", "text/plain")
      )
      .catch(() =>
        this.props.messageService("error", "Error", "failed to download data")
      )
      .finally(() => this.props.loadingService(false));
  };

  hideDialog = () => {
    this.setState({ downloadDialog: false, selectionRecords: 0 });
  };

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
      url: geoServerAPI,
      params: wmsParams,
      serverType: "geoserver",
      transition: 0,
      projection: "EPSG:3857"
    });

    this.api
      .restData()
      .getActiveLayersBoundingBox(layers)
      .then(response => {
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

        const reprojectedUL = transform(
          coordExtentUL,
          "EPSG:4326",
          "EPSG:3857"
        );
        const reprojectedLR = transform(
          coordExtentLR,
          "EPSG:4326",
          "EPSG:3857"
        );

        const ext = new boundingExtent([reprojectedUL, reprojectedLR]);
        this.map.getView().fit(ext);

        const olOnClickFunction = this.olGenerateGetFeatureInfoFunction;
        this.map.on("singleclick", olOnClickFunction);
      })
      .catch(() =>
        this.props.messageService(
          "error",
          "Error",
          "cannot get layer bounding box"
        )
      );
  }

  zoomToLayer(layer) {
    this.api
      .restData()
      .getLayerBoundingBox(layer)
      .then(response => {
        const upperLeft = response.data.upperLeftVertex;
        const lowerRight = response.data.lowerRightVertex;
        const coordExtentUL = [upperLeft.x, upperLeft.y];
        const coordExtentLR = [lowerRight.x, lowerRight.y];

        const reprojectedUL = transform(
          coordExtentUL,
          "EPSG:4326",
          "EPSG:3857"
        );
        const reprojectedLR = transform(
          coordExtentLR,
          "EPSG:4326",
          "EPSG:3857"
        );

        const ext = new boundingExtent([reprojectedUL, reprojectedLR]);
        this.map.getView().fit(ext);
      });
  }

  addRegressionDragBoxInteraction = () => {
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
        this.setState({ rbox: box });

        if (containsExtent(this.state.regressionExtent, extent)) {
          this.setState({
            regressionDialog: true
          });
        }
      }.bind(this)
    );

    this.map.addInteraction(dragBox);
  };

  zoomAndDrawRegressionBounds = () => {
    this.api
      .restData()
      .getRegressionBounds()
      .then(response => {
        const upperLeft = response.data.upperLeftVertex;
        const lowerRight = response.data.lowerRightVertex;
        const coordExtentUL = [upperLeft.x, upperLeft.y];
        const coordExtentLR = [lowerRight.x, lowerRight.y];

        const reprojectedUL = transform(
          coordExtentUL,
          "EPSG:4326",
          "EPSG:3857"
        );
        const reprojectedLR = transform(
          coordExtentLR,
          "EPSG:4326",
          "EPSG:3857"
        );

        const ext = new boundingExtent([reprojectedUL, reprojectedLR]);

        this.setState({
          regressionExtent: ext
        });

        const polygon = new Polygon.fromExtent(ext);
        const feature = new Feature(polygon);
        const vectorSource = new VectorSource();
        vectorSource.addFeature(feature);

        const vectorLayer = new VectorLayer({
          source: vectorSource
        });

        this.map.getView().fit(ext);
        this.map.addLayer(vectorLayer);
      });
  };

  turnRegressionMode = () => {
    this.props.messageService(
      "info",
      "Regression Service",
      "Select area inside bounds to calculate bathymetry with neural network"
    );
    this.zoomAndDrawRegressionBounds();
    this.map.un("singleclick", this.olGenerateGetFeatureInfoFunction);
    this.addRegressionDragBoxInteraction();
  };

  zoomFit = () => {
    const layers = this.getCol(this.props.layers, "id");

    if (layers.length === 0) {
      return;
    }

    this.api
      .restData()
      .getActiveLayersBoundingBox(layers)
      .then(response => {
        const upperLeft = response.data.upperLeftVertex;
        const lowerRight = response.data.lowerRightVertex;
        const coordExtentUL = [upperLeft.x, upperLeft.y];
        const coordExtentLR = [lowerRight.x, lowerRight.y];

        const reprojectedUL = transform(
          coordExtentUL,
          "EPSG:4326",
          "EPSG:3857"
        );
        const reprojectedLR = transform(
          coordExtentLR,
          "EPSG:4326",
          "EPSG:3857"
        );

        const ext = new boundingExtent([reprojectedUL, reprojectedLR]);
        this.map.getView().fit(ext);
      });
  };

  loadLayer(layer) {
    const wmsParams = {
      LAYERS: `bathymetry:${layer}`,
      TILED: true,
      STYLES: this.props.layerStyle
    };

    const wmsSource = new TileWMS({
      url: geoServerAPI,
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
      this.api
        .geoServerAPI()
        .getFeatureInfo(url)
        .then(response => {
          let features = response.data.features;

          if (features.length === 0) {
            return;
          }

          const transformedEvtCoords = transform(
            evt.coordinate,
            "EPSG:3857",
            "EPSG:4326"
          );

          let info = `coords:   ${transformedEvtCoords[0]} ${transformedEvtCoords[1]}`

          info += "\nmeasurement: " + features[0].properties.GRAY_INDEX;
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

  hideRegressionDialog = () => {
    this.setState({
      regressionDialog: false
    });
  };

  downloadRegression = () => {
    this.props.loadingService(true);
    this.api
      .restData()
      .downloadRegressionResults(this.state.rbox)
      .then(response =>
        downloadjs(response.data, "regressionData.csv", "text/plain")
      )
      .catch(() =>
        this.props.messageService("error", "Error", "failed to download data")
      )
      .finally(() => this.props.loadingService(false));
  };

  publishRegression = () => {
    this.props.loadingService(true);
    this.api
      .restData()
      .publishRegressionResults(this.state.rbox)
      .then(() =>
        this.props.messageService(
          "success",
          "Success",
          "data published successfully"
        )
      )
      .catch(error =>
        this.props.messageService("error", "Error", error.response.data.message)
      )
      .finally(() => {
        this.props.loadingService(false);
        this.setState({
          regressionDialog: false
        });
      });
  };

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

    const regressionFooter = (
      <div>
        <Button
          label="Publish"
          icon="pi pi-check"
          onClick={this.publishRegression}
        />
        <Button
          label="Download"
          icon="pi pi-download"
          onClick={this.downloadRegression}
        />
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
        <Dialog
          header="Regression Service"
          footer={regressionFooter}
          visible={this.state.regressionDialog}
          width="350px"
          modal={true}
          onHide={this.hideRegressionDialog}
        >
          Regression Service will publish bathymetry data raster with resolution
          based on selection size.
        </Dialog>
        <div id="map" style={{ height: "100%" }} />
      </div>
    );
  }
}
