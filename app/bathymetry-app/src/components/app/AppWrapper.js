import React from "react";
import PropTypes from "prop-types";
import { Sidebar } from "primereact/sidebar";
import { connect } from "react-redux";
import { HashRouter as Router, Route } from "react-router-dom";
import "./AppWrapper.scss";
import DataManager from "./datamanager/DataManager";
import DataSelector from "./dataselector/DataSelector";
import Map from "./map/Map";
import TopBar from "./topbar/TopBar";
import Settings from "./usersettings/Settings";
import { showPanel } from "./menu/MenuPanelActions";
import MenuPanel from "./menu/MenuPanel";

export function AppWrapperComponent(props) {
  return (
    <div>
      <Sidebar
        className="side-bar"
        visible={props.menuPanel}
        onHide={props.hidePanel}
        showCloseIcon={false}
      >
        <MenuPanel />
      </Sidebar>
      <TopBar />

      <div className="p-grid p-nogutter content-container">
        <Router>
          <div className="p-col-12">
            <Route exact path="/" component={Map} />
            <Route path="/select" component={DataSelector} />
            <Route path="/mydata" component={DataManager} />
            <Route path="/settings" component={Settings} />
          </div>
        </Router>
      </div>
    </div>
  );
}

AppWrapperComponent.propTypes = {
  menuPanel: PropTypes.bool.isRequired
};

const mapStateToProps = state => {
  return {
    menuPanel: state.menuPanel.showPanel
  };
};

const mapDispatchToProps = dispatch => {
  return {
    hidePanel: () => dispatch(showPanel(false))
  };
};

const AppWrapper = connect(
  mapStateToProps,
  mapDispatchToProps
)(AppWrapperComponent);
export default AppWrapper;
