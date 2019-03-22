import React, { Component } from "react";
import Cookies from "universal-cookie";
import { Growl } from "primereact/growl";

import LoginPage from "./components/login/LoginPage";
import AppWrapper from "./components/app/AppWrapper";

import "primereact/resources/primereact.min.css";
import "./theme/PrimeReactTheme.scss";
import "primeflex/primeflex.css";
import "primeicons/primeicons.css";
import "ol-layerswitcher/src/ol-layerswitcher.css";
import "./theme/Theme.css";
import "./theme/Utility.css";
import LoadingSpinner from "./components/utility/loading/Spinner";
import Message from "./components/utility/messaging/Message";

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoggedIn: false
    };

    this.changeLoginState = this.changeLoginState.bind(this);
    this.signIn = this.signIn.bind(this);
    this.signOut = this.signOut.bind(this);
    this.showMessage = this.showMessage.bind(this);
  }

  changeLoginState(loginState) {
    this.setState({
      isLoggedIn: loginState
    });
  }

  signIn() {
    this.changeLoginState(true);
  }

  signOut() {
    this.changeLoginState(false);
    const cookies = new Cookies();
    cookies.remove("access_token");
    cookies.remove("refresh_token");
  }

  showMessage(severity, title, message) {
    this.growl.show({
      severity: severity,
      summary: title,
      detail: message,
      closable: false
    });
  }

  render() {
    return (
      <div className="App">
        <LoadingSpinner />
        <Message />
        <Growl ref={ref => (this.growl = ref)} />
        {this.state.isLoggedIn ? (
          <AppWrapper
            messageService={this.showMessage}
            signOut={this.signOut}
          />
        ) : (
          <LoginPage messageService={this.showMessage} signIn={this.signIn} />
        )}
      </div>
    );
  }
}

export default App;
