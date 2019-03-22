import { ToggleButton } from "primereact/togglebutton";
import React, { Component } from "react";
import Cookies from "universal-cookie";
import API from "../../services/API";
import { LandingPageHeader } from "./layout/LandingPageHeader";
import { VerticalSpacer } from "./layout/VerticalSpacer";
import { LoginForm } from "./login/LoginForm";
import { RegisterForm } from "./register/RegisterForm";
import "./LoginPage.scss";
import LoginAsGuest from "./guest/LoginAsGuest";

export default class LandingPage extends Component {
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
    const cookies = new Cookies();
    if (cookies.get("access_token")) {
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

  

  saveTokens = response => {
    const cookies = new Cookies();

    const accessTokenExpireDate = new Date();
    accessTokenExpireDate.setDate(
      accessTokenExpireDate.getDate() + 60 * 60 * 1000
    );
    cookies.set("access_token", response.data.access_token, {
      expires: accessTokenExpireDate
    });

    const refreshTokenExpireDate = new Date();
    refreshTokenExpireDate.setDate(
      refreshTokenExpireDate.getDate() + 24 * 60 * 60 * 1000
    );
    cookies.set("refresh_token", response.data.refresh_token, {
      expires: refreshTokenExpireDate
    });
  };

  render() {
    return (
      <div className="loginControl">
        <div className="p-grid p-nogutter p-fluid">
          <LandingPageHeader />

          <div className="p-col-1 p-md-4" />
          <div className="p-col-10 p-md-4">
            {this.state.register ? (
              <RegisterForm
                toggleRegisterForm={this.toggleRegisterForm}
                messageService={this.props.messageService}
                loadingService={this.props.loadingService}
              />
            ) : (
              <LoginForm
                signIn={this.props.signIn}
                saveTokens={this.saveTokens}
                messageService={this.props.messageService}
                loadingService={this.props.loadingService}
              />
            )}
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
