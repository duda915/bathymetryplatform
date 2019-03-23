import React from "react";
import PropTypes from "prop-types";
import { Toolbar } from "primereact/toolbar";
import { Button } from "primereact/button";
import { ScrollPanel } from "primereact/scrollpanel";
import { handleRequest } from "../../../utility/requesthandler";
import Layer from "../layerswitcher/Layer";
import API from "../../../../services/API";
import "./Tools.scss";

export class ToolsComponent extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      showLayers: true
    };
  }

  componentDidMount() {
    this.fetchUsername();
  }

  fetchUsername() {
    const api = new API();

    handleRequest({
      requestPromise: api.restUser().getUser(),
      onSuccess: response =>
        this.setState({ username: response.data.username }),
      onErrorMessage: () => "failed to fetch username"
    });
  }

  render() {
    return (
      <div className="p-grid p-nogutter tools-username tools-container">
        <div className="p-col-12">
          <div className="p-grid p-nogutter tools-username-box">
            <div className="p-col-5" style={{ textAlign: "right" }}>
              <i className="pi pi-user" />
            </div>
            <div className="p-col-1 tools-username-font">
              {this.state.username}
            </div>
          </div>
        </div>

        <div className="p-col-12">
          <Toolbar className="tools-toolbar">
            <div className="p-toolbar-group-left">
              <Button
                style={{ marginRight: "10px" }}
                className="p-button tools-button flat-button"
                onClick={this.props.signOut}
                icon="pi pi-sign-out"
              />
              <Button
                style={{ marginRight: "10px" }}
                className="p-button tools-button flat-button"
                onClick={this.props.changeStyle}
                icon="pi pi-eye"
              />
              <Button
                style={{ marginRight: "10px" }}
                className="p-button tools-button flat-button"
                onClick={() =>
                  this.setState({ showLayers: !this.state.showLayers })
                }
                icon="pi pi-image"
              />
              <Button
                className="p-button tools-button flat-button"
                onClick={this.props.zoomFit}
                icon="pi pi-search"
              />
            </div>
          </Toolbar>
        </div>

        {this.state.showLayers ? (
          <div className="p-col-12 layer-tool">
            <h6
              className="layer-tool-header"
              style={{
                marginTop: "5px",
                marginBottom: "5px",
                fontSize: "12px"
              }}
            >
              Layers
            </h6>
            <ScrollPanel style={{ height: "100px" }}>
              {this.props.layers.map(layer => (
                <Layer
                  key={layer.id}
                  layer={layer}
                  zoomToLayer={this.props.zoomToLayer}
                  toggleLayer={this.props.toggleLayer}
                />
              ))}
            </ScrollPanel>
          </div>
        ) : null}
      </div>
    );
  }
}

ToolsComponent.propTypes = {
  layers: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      visible: PropTypes.bool.isRequired
    }).isRequired
  ),
  signOut: PropTypes.func.isRequired,
  zoomFit: PropTypes.func.isRequired,
  changeStyle: PropTypes.func.isRequired,
  zoomToLayer: PropTypes.func.isRequired,
  toggleLayer: PropTypes.func.isRequired
};
