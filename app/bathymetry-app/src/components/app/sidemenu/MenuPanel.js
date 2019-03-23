import React, { Component } from "react";
import { HashRouter } from "react-router-dom";
import MenuButton from "./menubutton/MenuButton";
import MenuButtonOnClick from "./menubutton/MenuButtonOnClick";
import "./MenuPanel.scss";
import Tools from "./menutoolbar/Tools";
import API from "../../../services/API";
import { handleRequest } from "../../utility/requesthandler";

class MenuPanel extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentWillMount() {
    this.checkUser();
  }

  checkUser = () => {
    const api = new API();

    handleRequest({
      requestPromise: api.restUser().getUser(),
      onSuccess: response => {
        if (response.data.username === "guest") {
          this.setState({ guest: true });
        }
      }
    });
  };

  render() {
    return (
      <div className="p-grid p-nogutter menu-panel">
        <div className="p-col-12 brand" />
        <div className="p-col-12 " style={{ padding: "15px" }}>
          <Tools />
        </div>

        <div className="p-col-12 p-col-align-center">
          <HashRouter>
            <div className="router-container menu-begin">
              <div className="menu-top">
                <div className="menu-header">Menu</div>
              </div>
              <MenuButton to="/" label="Map" icon="pi pi-map-marker" />
              <MenuButton
                to="/select"
                label="Select Data"
                icon="pi pi-search"
              />
              <MenuButton
                to="/mydata"
                disabled={this.state.guest}
                label="My Data"
                icon="pi pi-cloud-upload"
              />
            </div>
          </HashRouter>
        </div>
        <div className="p-col-12 menu-spacer" style={{ height: "50px" }} />

        <div className="p-col-12 p-col-align-center">
          <HashRouter>
            <div className="router-container">
              <MenuButton
                to="/settings"
                disabled={this.state.guest}
                label="Settings"
                icon="pi pi-cog"
              />
              <MenuButtonOnClick
                to="/"
                label="Logout"
                icon="pi pi-sign-out"
                onClick={this.props.signOut}
              />
            </div>
          </HashRouter>
        </div>
      </div>
    );
  }
}

export default MenuPanel;
