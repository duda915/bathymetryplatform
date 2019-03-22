import React, { Component } from "react";
import Cookies from "universal-cookie";

import "primereact/resources/primereact.min.css";
import "./theme/PrimeReactTheme.scss";
import "primeflex/primeflex.css";
import "primeicons/primeicons.css";
import "./theme/Theme.css";
import "./theme/Utility.css";

import LoadingSpinner from "./components/utility/loading/Spinner";
import Message from "./components/utility/messaging/Message";
import EntryPoint from "./components/EntryPoint";

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoggedIn: false
    };

    this.changeLoginState = this.changeLoginState.bind(this);
    this.signIn = this.signIn.bind(this);
    this.signOut = this.signOut.bind(this);
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

  render() {
    return (
      <div className="App">
        <LoadingSpinner />
        <Message />
        <EntryPoint />
      </div>
    );
  }
}

export default App;
