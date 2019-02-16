import { ToggleButton } from "primereact/togglebutton";
import React, { Component } from "react";
import "./LandingPage.scss";
import { LandingPageHeader } from "./layout/LandingPageHeader";
import { LoginForm } from "./login/LoginForm";
import { RegisterForm } from "./register/RegisterForm";
import UserService from "../../services/UserService";
import { Button } from "primereact/button";

import API from "../../services/API";
import Cookies from "universal-cookie";

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
      this.props.loadingService(true);
      this.api
        .restUser()
        .getUser()
        .then(response => this.props.signIn())
        .catch(error => console.log("auto login not possible"))
        .finally(this.props.loadingService(false));
    }
  };

  toggleRegisterForm = boolean => {
    this.setState({
      register: boolean
    });
  };

  loginAsGuest = () => {
    this.props.loadingService(true);

    this.api
      .restUser()
      .loginUser("guest", "guest")
      .then(response => {
        this.saveTokens(response);
        this.props.signIn();
      })
      .catch(error => this.props.messageService("error", "Error", error))
      .finally(this.props.loadingService(false));
  };

  saveTokens(response) {
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
  }

  render() {
    return (
      <div className="loginControl">
        <div className="p-grid p-nogutter p-fluid">
          <LandingPageHeader />
          <div className="p-col-4" />
          <div className="p-col-4">
            {this.state.register ? (
              <RegisterForm
                toggleRegisterForm={this.toggleRegisterForm}
                messageService={this.props.messageService}
                loadingService={this.props.loadingService}
              />
            ) : (
              <LoginForm
                signIn={this.props.signIn}
                messageService={this.props.messageService}
                loadingService={this.props.loadingService}
              />
            )}
          </div>
          <div className="p-col-4" />
          <div className="p-col-12" style={{ height: "20px" }} />

          <div className="p-col-4" />
          <div className="p-col-4">
            <ToggleButton
              offLabel="Register"
              onLabel="Register"
              offIcon="pi pi-user-plus"
              onIcon="pi pi-user-plus"
              checked={this.state.register}
              onChange={e => this.toggleRegisterForm(e.value)}
            />
          </div>
          <div className="p-col-4" />

          <div className="p-col-12" style={{ height: "20px" }} />

          <div className="p-col-4" />
          <div className="p-col-4">
            <Button label="Login as guest" onClick={this.loginAsGuest} />
          </div>
          <div className="p-col-4" />
        </div>
      </div>
    );
  }
}
