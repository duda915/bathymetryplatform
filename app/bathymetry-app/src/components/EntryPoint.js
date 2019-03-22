import React from "react";
import { connect } from "react-redux";

import LoginPage from "./login/LoginPage";
import AppWrapper from "./app/AppWrapper";

const mapStateToProps = state => {
  return {
    isLoggedIn: state.login.loginState
  };
};

const EntryPoint = connect(mapStateToProps)(EntryPointComponent);
export default EntryPoint;

function EntryPointComponent(props) {
  return <div>{props.isLoggedIn ? <AppWrapper /> : <LoginPage />}</div>;
}
