import React from "react";
import { Toolbar } from "primereact/toolbar";
import UserService from "../../../services/UserService";
import "./Tools.scss";
import { Button } from "primereact/button";
import { ScrollPanel } from "primereact/scrollpanel";

import { Layer } from "./Layer";

export default class Tools extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      username: "",
      showLayers: true
    };

    this.userService = new UserService();
  }

  componentDidMount() {
    this.fetchUsername();
  }

  fetchUsername() {
    this.userService
      .getUser()
      .then(response => this.setState({ username: response.data.username }));
  }

  render() {
    return (
      <div className="p-grid p-nogutter tools-username">
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
                className="p-button-warning"
                onClick={this.props.signOut}
                icon="pi pi-sign-out"
              />
              <Button
                style={{ marginRight: "10px" }}
                className="p-button-warning"
                onClick={this.props.changeStyle}
                icon="pi pi-eye"
              />
              <Button
                className="p-button-warning"
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
            <ScrollPanel style={{ height: "100px" }}>
              <h6
                style={{
                  marginTop: "5px",
                  marginBottom: "5px",
                  fontSize: "12px"
                }}
              >
                Layers
              </h6>
              {this.props.selectedLayers.map(layerId => 
                <Layer layerName={layerId} />
                )}
            </ScrollPanel>
          </div>
        ) : null}

      </div>
    );
  }
}
