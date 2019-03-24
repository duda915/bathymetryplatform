import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import "./TopBar.scss";
import { togglePanel } from "../menu/MenuPanelActions";
import { changeLoginState } from "../../login/LoginActions";
import { Button } from "primereact/button";
import { removeTokens } from "../../../services/Token";
import { HashRouter } from "react-router-dom";
import { NavLink } from "react-router-dom";

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
              <div className="top-bar__button" onClick={props.togglePanel}>
                <i className="pi pi-bars" />
            </div>
          </div>
        </div>
        <HashRouter>
          <NavLink
            className="p-col-6 top-bar__brand"
            to="/"
            onClick={e => e.preventDefault()}
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
  togglePanel: PropTypes.func.isRequired,
  signOut: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    togglePanel: () => dispatch(togglePanel()),
    signOut: () => dispatch(changeLoginState(false))
  };
};

const TopBar = connect(
  mapStateToProps,
  mapDispatchToProps
)(TopBarComponent);

export default TopBar;
