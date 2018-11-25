import React, { Component } from 'react';
import {
    HashRouter as Router,
    Route,
} from 'react-router-dom';
import CSSTransition from 'react-transition-group/CSSTransition';

import { ScrollPanel } from 'primereact/scrollpanel';

import MapComponent from './mainpanels/MapComponent';
import MenuPanel from './MenuPanel';
import TopBar from './TopBar';
import DataComponent from './mainpanels/DataComponent';
import UserService from '../services/UserService';
import DataManager from './mainpanels/DataManager';


class MainWindow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            menuVisible: true,
            selectedLayers: [0],
        };

        this.handleLogout = this.handleLogout.bind(this);
        this.togglePanel = this.togglePanel.bind(this);

        this.mapReference = React.createRef();
        this.userService = new UserService();

        this.loadSelectedLayers = this.loadSelectedLayers.bind(this);
    }

    componentDidMount() {
        this.fetchUsername();
    }

    fetchUsername() {
        this.userService.getUser()
            .then(response => this.setState({ username: response.data }));
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
        if (this.mapReference.current != null) {
            this.mapReference.current.updateMapSize();
        }
    }

    loadSelectedLayers(ids) {
        this.setState({
            selectedLayers: ids,
        })
    }

    render() {
        return (
            <div className="mainWindow">
                <div className="p-grid p-nogutter">

                    {/* left menu */}
                    <CSSTransition in={this.state.menuVisible} appear={true} timeout={500} classNames="menuslide" onEntered={() => this.tryMapSizeUpdate()}
                        onExited={() => this.tryMapSizeUpdate()}>
                        <ScrollPanel className="p-col-fixed menuslide-init leftmenu">

                            <div className="p-grid p-justify-center">
                                <div className="p-col-12" style={{ height: '10vh' }} />
                                <div className="p-col ">
                                    <MenuPanel logoutFun={this.handleLogout} />
                                </div>
                            </div>
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
                                        return (
                                            <MapComponent ref={this.mapReference} layers={this.state.selectedLayers} />
                                        )
                                    }} />
                                    <Route path="/select" render={() => {
                                        return (
                                            <DataComponent loadLayersFun={this.loadSelectedLayers} />
                                        )
                                    }} />
                                    <Route path="/mydata" component={DataManager} />
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