import React, { Component } from 'react';
import {
    HashRouter as Router,
    Route,
    Link
  } from 'react-router-dom';
import CSSTransition from 'react-transition-group/CSSTransition';

import { ScrollPanel } from 'primereact/scrollpanel';

import MapComponent from './mainpanels/MapComponent';
import MenuPanel from './MenuPanel';
import TopBar from './TopBar';
import AddData from './mainpanels/AddData';
import MapMenu from './mainpanels/MapMenu';
import DataComponent from './mainpanels/DataComponent';
import UserService from '../services/UserService';


class MainWindow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            menuVisible: true,
        };

        this.handleLogout = this.handleLogout.bind(this);
        this.togglePanel = this.togglePanel.bind(this);

        this.mapRefreshRef = React.createRef();
        this.userService = new UserService();
    }

    componentDidMount() {
        this.fetchUsername();
    }

    fetchUsername() {
        this.userService.getUser()
        .then(response => this.setState({username: response.data}));
    }

    handleLogout() {
        this.userService.logoutUser()
        .then(response => this.props.changeLoginState(false));
    }

    togglePanel() {
        this.setState({
            menuVisible: !this.state.menuVisible,
        });

    }

    tryMapSizeUpdate() {
        if (this.mapRefreshRef.current != null) {
            this.mapRefreshRef.current.updateMapSize();
        }
    }

    render() {
        return (
            <div className="mainWindow">
                <div className="p-grid p-nogutter">

                    {/* left menu */}
                    <CSSTransition in={this.state.menuVisible} appear={true} timeout={500} classNames="menuslide" onEntered={() => this.tryMapSizeUpdate()}
                        onExited={() => this.tryMapSizeUpdate()}>
                        <ScrollPanel className="p-col-fixed menuslide-init ">
                            <MenuPanel logoutFun={this.handleLogout} />
                        </ScrollPanel>
                    </CSSTransition>

                    <div className="p-col">
                        <div className="p-grid p-nogutter">
                            <div className="p-col-12" style={{ height: '50px' }}>
                                {/* main bar */}
                                <TopBar togglePanel={this.togglePanel} logoutFun={this.handleLogout} />
                            </div>
                            {/* main panel */}
                                <Router>
                                    <div className="p-col-12" style={{ height: 'calc(100vh - 50px)' }}> 
                                        <Route exact path="/" render={() => {
                                            return(
                                                <MapComponent ref={this.mapRefreshRef}/>
                                            )
                                        }} />
                                        <Route path="/add" component={DataComponent}/>
                                        <Route path="/select" component={MapMenu}/>
                                    </div>
                                </Router>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default MainWindow;