import { ToggleButton } from "primereact/togglebutton";
import React, { Component } from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import API from "../../services/API";
import { LoginPageHeader } from "./layout/LoginPageHeader";
import { VerticalSpacer } from "./layout/VerticalSpacer";
import LoginForm from "./login/LoginForm";
import { RegisterForm } from "./register/RegisterForm";
import "./LoginPage.scss";
import LoginAsGuest from "./guest/LoginAsGuest";
import { getToken } from "../../services/Token";
import { changeLoginState } from "./LoginActions";

export class LoginPageComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      register: false
    };

    this.api = new API();
  }

  componentDidMount() {
    this.tryLogin();
  }

  tryLogin = () => {
    if (getToken()) {
      this.api
        .restUser()
        .getUser()
        .then(() => this.props.signIn())
        .catch(() => console.log("auto login not possible"));
    }
  };

  toggleRegisterForm = boolean => {
    this.setState({
      register: boolean
    });
  };

  render() {
    return (
      <div className="login-page">
        <div className="p-grid p-nogutter p-fluid">
          <LoginPageHeader />

          <div className="p-col-1 p-md-4" />
          <div className="p-col-10 p-md-4">
            {this.state.register ? <RegisterForm /> : <LoginForm />}
          </div>
          <div className="p-col-1 p-md-4" />

          <VerticalSpacer />

          <div className="p-col-1 p-md-4" />
          <div className="p-col-10 p-md-4">
            <ToggleButton
              offLabel="Register"
              onLabel="Register"
              offIcon="pi pi-user-plus"
              onIcon="pi pi-user-plus"
              checked={this.state.register}
              onChange={e => this.toggleRegisterForm(e.value)}
            />
          </div>
          <div className="p-col-1 p-md-4" />

          <VerticalSpacer />

          <div className="p-col-1 p-md-4" />
          <div className="p-col-10 p-md-4">
            <LoginAsGuest />
          </div>
          <div className="p-col-1 p-md-4" />
        </div>
      </div>
    );
  }
}

LoginPageComponent.propTypes = {
  signIn: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    // signIn: () => dispatch(changeLoginState(true))
  };
};

const LoginPage = connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginPageComponent);

export default LoginPage;
