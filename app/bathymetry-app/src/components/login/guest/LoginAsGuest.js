import { Button } from "primereact/button";
import PropTypes from "prop-types";
import React, { Component } from "react";
import { connect } from "react-redux";
import API from "../../../services/API";
import { saveTokens } from "../../../services/Token";
import { handleRequest } from "../../utility/requesthandler";
import { changeLoginState } from "../LoginActions";

export class LoginAsGuestComponent extends Component {
  loginAsGuest = () => {
    const api = new API();

    handleRequest({
      requestPromise: api.restUser().loginUser("guest", "guest"),
      onSuccess: response => {
        saveTokens(response);
        this.props.setLoggedInState();
      },
      onErrorMessage: () => "failed to login"
    });
  };

  render() {
    return (
      <div>
        <Button label="Login as guest" onClick={this.loginAsGuest} />
      </div>
    );
  }
}

LoginAsGuestComponent.propTypes = {
  setLoggedInState: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    setLoggedInState: () => dispatch(changeLoginState(true))
  };
};

const LoginAsGuest = connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginAsGuestComponent);

export default LoginAsGuest;
