import PropTypes from "prop-types";
import React from "react";
import { connect } from "react-redux";
import { HashRouter, NavLink } from "react-router-dom";
import { removeTokens } from "../../../services/Token";
import { changeLoginState } from "../../login/LoginActions";
import { showPanel } from "../menu/MenuPanelActions";
import "./TopBar.scss";

export function TopBarComponent(props) {
  const signOut = () => {
    removeTokens();
    props.signOut();
  };

  return (
    <div className="top-bar">
      <div className="p-grid p-nogutter ">
        <div className="p-col-3 ">
          <div className="p-grid p-nogutter">
              <div className="top-bar__button" onClick={props.showPanel}>
                <i className="pi pi-bars" />
            </div>
          </div>
        </div>
        <HashRouter>
          <NavLink
            className="p-col-6 top-bar__brand"
            exact
            to="/"
          />
        </HashRouter>
        <div className="p-col-3">
          <div className="p-grid p-nogutter p-justify-end">
              <div className="top-bar__button" onClick={signOut}>
                <i className="pi pi-sign-out" />
              </div>
          </div>
        </div>
      </div>
    </div>
  );
}

TopBarComponent.propTypes = {
  showPanel: PropTypes.func.isRequired,
  signOut: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    showPanel: () => dispatch(showPanel(true)),
    signOut: () => dispatch(changeLoginState(false))
  };
};

const TopBar = connect(
  mapStateToProps,
  mapDispatchToProps
)(TopBarComponent);

export default TopBar;
