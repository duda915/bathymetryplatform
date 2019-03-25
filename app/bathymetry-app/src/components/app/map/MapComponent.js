import downloadjs from "downloadjs";
import PropTypes from "prop-types";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import React, { Component } from "react";
import API from "../../../services/API";
import ConnectedBathymetryMap from "./ConnectedBathymetryMap";
import { handleRequest } from "../../utility/requesthandler";
import { Commands } from "./MapCommands";
import "./Map.scss";

export class MapComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      downloadDialog: false,
      selectionRecords: 0
    };
  }

  componentDidMount() {
    this.map = new ConnectedBathymetryMap(this.props.layers, this.props.style);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.command !== this.props.command) {
      this.handleCommand();
    }

    if (prevProps.layers !== this.props.layers) {
      this.map.setLayers(this.props.layers);
    }

    if (prevProps.style !== this.props.style) {
      this.map.setStyle(this.props.style);
    }
  }

  handleCommand() {
    const { commandType, commandPayload } = this.props.command;
    const api = new API();

    switch (commandType) {
      case Commands.FETCH_FEATURE_INFO:
        handleRequest({
          requestPromise: api.geoServerAPI().getFeatureInfo(commandPayload.url),
          onSuccessMessage: response => {
            return `lat: ${commandPayload.coordinate[0]} lon: ${
              commandPayload.coordinate[1]
            } Measurement ${response.data.features[0].properties.GRAY_INDEX}`;
          }
        });
        return;

      case Commands.HANDLE_DRAG_BOX:
        if (this.state.regressionService) {
          if (this.map.checkIfBoxContainsInRegressioExtent(commandPayload)) {
            this.setState({
              regressionDialog: true,
              regressionSelection: commandPayload
            });
          }
          return;
        }

        if (this.map.getVisibleLayers().length === 0) {
          return;
        }

        handleRequest({
          requestPromise: api
            .restData()
            .countSelectedDataSets(
              this.map.getVisibleLayers().map(layer => layer.id),
              commandPayload
            ),
          onSuccess: response => {
            if (response.data.response === "0") {
              return;
            }

            this.setState({
              downloadDialog: true,
              selectionRecords: response.data.response,
              downloadBox: commandPayload
            });
          },
          onError: error => console.log(error.response)
        });
        return;

      case Commands.ZOOM_TO_FIT:
        this.map.zoomToFit();
        return;

      case Commands.ZOOM_TO_LAYER:
        this.map.zoomToLayer(commandPayload);
        return;

      case Commands.TURN_ON_REGRESSION_SERVICE_INTERACTION:
        this.setState({ regressionService: true });
        this.map.turnOnRegressionService();
        return;
      default:
        console.log("unknown command");
    }
  }

  downloadAccept = () => {
    this.setState({ downloadDialog: false, selectionRecords: 0 });

    const layersIds = this.props.layers.map(layer => layer.id);
    const api = new API();

    handleRequest({
      requestPromise: api
        .restData()
        .downloadSelectedDataSets(layersIds, this.state.downloadBox),
      onSuccess: response =>
        downloadjs(response.data, "bathymetry_selection.csv", "text/plain"),
      onError: error => console.log(error.response),
      onErrorMessage: () => "failed to download data"
    });
  };

  hideDialog = () => {
    this.setState({ downloadDialog: false, selectionRecords: 0 });
  };

  hideRegressionDialog = () => {
    this.setState({ regressionDialog: false });
  };

  publishRegression = () => {
    const api = new API();

    handleRequest({
      requestPromise: api
        .restData()
        .publishRegressionResults(this.state.regressionSelection),
      onSuccessMessage: () => "data published",
      onError: error => console.log(error.response)
    });

    this.hideRegressionDialog();
  };

  downloadRegression = () => {
    const api = new API();

    handleRequest({
      requestPromise: api
        .restData()
        .downloadRegressionResults(this.state.regressionSelection),
      onSuccess: response =>
        downloadjs(response.data, "regression.csv", "text/plain")
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
      <>
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
        <div id="map" className="map-container" />
      </>
    );
  }
}

MapComponent.propTypes = {
  layers: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      visible: PropTypes.bool.isRequired
    }).isRequired
  ),
  command: PropTypes.object,
  style: PropTypes.string.isRequired
};
