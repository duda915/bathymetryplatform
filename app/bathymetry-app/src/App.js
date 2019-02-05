import React, { Component } from 'react';
import LoginControl from './components/login/LoginControl';
import MainWindow from './components/MainWindow';
import 'primereact/resources/primereact.min.css';
import './theme/PrimeReactTheme.scss';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';

import './theme/Theme.css';
import './theme/Utility.css';

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoggedIn: false,
    };

    this.changeLoginState = this.changeLoginState.bind(this);
  }

  changeLoginState(loginBoolean) {
    this.setState({
      isLoggedIn: loginBoolean,
    });
  }

  render() {
    return (
      <div className="App" >
        {this.state.isLoggedIn ? <MainWindow changeLoginState={this.changeLoginState}/> : <LoginControl changeLoginState={this.changeLoginState}/>}
      </div>
    );
  }
}

export default App;
