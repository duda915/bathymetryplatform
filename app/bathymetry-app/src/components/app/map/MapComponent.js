import downloadjs from "downloadjs";
import PropTypes from "prop-types";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import React, { Component } from "react";
import API from "../../../services/API";
import ConnectedBathymetryMap from "./ConnectedBathymetryMap";
import { handleRequest } from "../../utility/requesthandler";

export default class MapComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      downloadDialog: false,
      selectionRecords: 0
    };
  }

  componentDidMount() {
    this.map = new ConnectedBathymetryMap(this.props.layers);
  }

  downloadAccept = () => {
    this.setState({ downloadDialog: false, selectionRecords: 0 });

    const layersIds = this.props.layers.map(layer => layer.id);
    const api = new API();

    handleRequest({
      requestPromise: api.restData().downloadSelectedDataSets(layersIds),
      onSuccess: response =>
        downloadjs(response.data, "bathymetry_selection.csv", "text/plain"),
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
  layers: PropTypes.shape({
    id: PropTypes.number.isRequired,
    visible: PropTypes.bool.isRequired
  })
};
