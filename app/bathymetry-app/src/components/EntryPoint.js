import React from "react";
import LoginPage from "./login/LoginPage";
import AppWrapper from "./app/AppWrapper";

export default function EntryPoint(props) {
  return <div>{props.isLoggedIn ? <AppWrapper /> : <LoginPage />}</div>;
}
