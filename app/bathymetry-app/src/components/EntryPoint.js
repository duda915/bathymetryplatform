import React, { Component } from "react";
import LoginPage from "./login/LoginPage";
import AppWrapper from "./app/AppWrapper";

export default class EntryPoint extends Component {
  render() {
    return <div>{this.props.isLoggedIn ? <AppWrapper /> : <LoginPage />}</div>;
  }
}
