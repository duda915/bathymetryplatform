import React, { Component } from 'react';
import LoginControl from './components/LoginControl';
import MainWindow from './components/MainWindow';
import 'primereact/resources/primereact.min.css';
import 'primereact/resources/themes/nova-colored/theme.css';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';

import './components/utility/Utility.css';
import './components/utility/Theme.css';

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
