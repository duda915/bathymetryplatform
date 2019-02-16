import React, { Component } from "react";
import { HashRouter } from "react-router-dom";
import UserService from "../../services/UserService";
import MenuButton from "./menubutton/MenuButton";
import MenuButtonOnClick from "./menubutton/MenuButtonOnClick";
import "./MenuPanel.scss";
import Tools from "./menutoolbar/Tools";

class MenuPanel extends Component {
  constructor(props) {
    super(props);

    this.state = {
      menuItems: []
    };

    this.userService = new UserService();

    this.changeStyle = this.changeStyle.bind(this);
  }

  componentWillMount() {
    this.checkUser();
  }

  changeStyle() {
    this.props.changeStyle();
  }

  checkUser() {
    this.userService.getUser().then(response => {
      if (response.data.username === "guest") {
        this.setState({
          guest: true
        });
      }
    });
  }

  render() {
    return (
      <div className="p-col-fixed menuslide-init menupanel">
        <div className="p-grid p-nogutter">
          <div className="p-col-12 brand" />
          <div className="p-col-12 " style={{ padding: "15px" }}>
            <Tools
              signOut={this.props.signOut}
              changeStyle={this.changeStyle}
              selectedLayers={this.props.selectedLayers}
              toggleLayer={this.props.toggleLayer}
              zoomToLayer={this.props.zoomToLayer}
            />
            {/* <ToggleButton offLabel="Style" onLabel="Style"
                            checked={this.state.toggleStyleButton} onChange={this.changeStyle} onIcon='pi pi-eye' offIcon='pi pi-eye' /> */}
          </div>

          <div className="p-col-12 p-col-align-center">
            {/* <Menu model={this.state.menuItems} className="menu" /> */}
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
      </div>
    );
  }
}

export default MenuPanel;