import React from "react";
import { Toolbar } from "primereact/toolbar";
import { Button } from "primereact/button";
import { ScrollPanel } from "primereact/scrollpanel";

import { Layer } from "../layerswitcher/Layer";
import API from "../../../services/API";
import "./Tools.scss";

export default class Tools extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      username: "",
      showLayers: true
    };

    this.api = new API();
  }

  componentDidMount() {
    this.fetchUsername();
  }

  fetchUsername() {
    this.api
      .restUser()
      .getUser()
      .then(response => this.setState({ username: response.data.username }))
      .catch(() =>
        this.props.messageService("error", "Error", "failed to fetch username")
      );
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
                className="p-button tools-button flat-button"
                onClick={e =>
                  this.setState({ showLayers: !this.state.showLayers })
                }
                icon="pi pi-image"
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
              {this.props.selectedLayers.map(layer => (
                <Layer
                  key={layer.id}
                  layer={layer}
                  toggleLayer={this.props.toggleLayer}
                  zoomToLayer={this.props.zoomToLayer}
                />
              ))}
            </ScrollPanel>
          </div>
        ) : null}
      </div>
    );
  }
}
