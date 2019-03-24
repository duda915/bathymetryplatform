import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import "./TopBar.scss";
import { togglePanel } from "../menu/MenuPanelActions";
import { changeLoginState } from "../../login/LoginActions";
import { Button } from "primereact/button";
import { removeTokens } from "../../../services/Token";

export function TopBarComponent(props) {
  const signOut = () => {
    removeTokens();
    props.signOut();
  };

  return (
    <div className="top-bar" style={{ height: "50px" }}>
      <div className="p-grid p-nogutter p-justify-between">
        <div className="p-col-2">
          <Button icon="pi pi-bars" onClick={props.togglePanel} />
        </div>
        <div className="p-col-4 top-bar__brand"></div>
        <div className="p-col-2">
          <Button icon="pi pi-sign-out" onClick={signOut} />
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
