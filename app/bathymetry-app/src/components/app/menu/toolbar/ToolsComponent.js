import { Button } from "primereact/button";
import { Toolbar } from "primereact/toolbar";
import PropTypes from "prop-types";
import React from "react";
import API from "../../../../services/API";
import { handleRequest } from "../../../utility/requesthandler";
import LayerSwitcher from "../layerswitcher/LayerSwitcher";
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
              {this.props.layers.length === 0 ? null : (
                <>
                  <Button
                    className="p-button tools-button flat-button"
                    onClick={() =>
                      this.setState({ showLayers: !this.state.showLayers })
                    }
                    icon="pi pi-list"
                  />
                  <Button
                    className="p-button tools-button flat-button"
                    onClick={this.props.changeStyle}
                    icon="pi pi-eye"
                  />
                  <Button
                    className="p-button tools-button flat-button"
                    onClick={this.props.zoomFit}
                    icon="pi pi-search"
                  />
                </>
              )}

              <Button
                className="p-button tools-button flat-button"
                onClick={this.props.turnOnRegressionServiceInteraction}
                icon="pi pi-paperclip"
              />
            </div>
          </Toolbar>
        </div>
        {!this.state.showLayers || this.props.layers.length === 0 ? null : (
          <LayerSwitcher />
        )}
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
  turnOnRegressionServiceInteraction: PropTypes.func.isRequired
};
