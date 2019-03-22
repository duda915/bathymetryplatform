import React from "react";
import { connect } from "react-redux";
import PropTypes from 'prop-types'
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

EntryPointComponent.propTypes = {
  isLoggedIn: PropTypes.bool.isRequired
} 