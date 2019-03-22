import React, { Component } from "react";
import PropTypes from "prop-types";
import { Button } from "primereact/button";
import { connect } from "react-redux";
import { changeLoginState } from "../LoginActions";
import { toggleSpinner } from "../../utility/loading/SpinnerActions";
import { showMessage } from "../../utility/messaging/MessageActions";
import { saveTokens } from "../../../services/Token";
import API from "../../../services/API";

export class LoginAsGuestComponent extends Component {
  constructor(props) {
    super(props);

    this.api = new API();
  }

  loginAsGuest = () => {
    this.props.spinner(true);

    this.api
      .restUser()
      .loginUser("guest", "guest")
      .then(response => {
        saveTokens(response);
        this.props.setLoggedInState();
      })
      .catch(() => this.props.message("error", "Error", "failed to login"))
      .finally(() => this.props.spinner(false));
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
  setLoggedInState: PropTypes.func.isRequired,
  message: PropTypes.func.isRequired,
  spinner: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    setLoggedInState: () => dispatch(changeLoginState(true)),
    spinner: show => dispatch(toggleSpinner(show)),
    message: (severity, summary, detail) =>
      dispatch(showMessage(severity, summary, detail))
  };
};

const LoginAsGuest = connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginAsGuestComponent);

export default LoginAsGuest;
