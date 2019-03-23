import downloadjs from "downloadjs";
import PropTypes from "prop-types";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import React, { Component } from "react";
import API from "../../../services/API";
import ConnectedBathymetryMap from "./ConnectedBathymetryMap";
import { handleRequest } from "../../utility/requesthandler";
import { Commands } from "./MapCommands";

export class MapComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      downloadDialog: false,
      selectionRecords: 0
    };
  }

  componentDidMount() {
    this.map = new ConnectedBathymetryMap(this.props.layers);
    console.log("mount");
  }

  componentDidUpdate(prevProps) {
    if (prevProps.command !== this.props.command) {
      this.handleCommand();
    }

    if (prevProps.layers !== this.props.layers) {
      this.map.setLayers(this.props.layers);
    }
  }

  handleCommand() {
    const { commandType, commandPayload } = this.props.command;
    const api = new API();

    console.log(commandPayload);

    switch (commandType) {
      case Commands.FETCH_FEATURE_INFO:
        handleRequest({
          requestPromise: api.geoServerAPI().getFeatureInfo(commandPayload.url),
          onSuccessMessage: response => {
            return `lat: ${commandPayload.coordinate[0]} lon: ${
              commandPayload.coordinate[1]
            } Measurement ${response.data.features[0].properties.GRAY_INDEX}`;
          },
          onError: error => console.log(error)
        });
        return;

      case Commands.HANDLE_DRAG_BOX:
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

      case Commands.TOGGLE_STYLE:
        this.map.toggleStyle();
        return;

      case Commands.ZOOM_TO_FIT:
        this.map.zoomToFit();
        return;

      case Commands.ZOOM_TO_LAYER:
        this.map.zoomToLayer(commandPayload);
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
        <div id="map" style={{ height: "100%" }} />
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
  command: PropTypes.object
};
