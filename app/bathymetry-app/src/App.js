import React, { Component } from "react";
import LandingPage from "./components/landingpage/LandingPage";
import "primereact/resources/primereact.min.css";
import "./theme/PrimeReactTheme.scss";
import "primeflex/primeflex.css";
import "primeicons/primeicons.css";
import "ol-layerswitcher/src/ol-layerswitcher.css";
import "./theme/Theme.css";
import "./theme/Utility.css";
import { Growl } from "primereact/growl";
import LoadingComponent from "./components/utility/LoadingComponent";
import MainWindow from "./components/MainWindow";
import Cookies from "universal-cookie";

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
    this.showLoading = this.showLoading.bind(this);
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

  showLoading(boolean) {
    this.progress.showLoading(boolean);
  }

  render() {
    return (
      <div className="App">
        <Growl ref={ref => (this.growl = ref)} />
        <LoadingComponent ref={ref => (this.progress = ref)} />
        {this.state.isLoggedIn ? (
          <MainWindow
            messageService={this.showMessage}
            loadingService={this.showLoading}
            signOut={this.signOut}
          />
        ) : (
          <LandingPage
            messageService={this.showMessage}
            loadingService={this.showLoading}
            signIn={this.signIn}
          />
        )}
      </div>
    );
  }
}

export default App;
