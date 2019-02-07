import React, { Component } from 'react';
import {
    HashRouter as Router,
    Route,
} from 'react-router-dom';
import CSSTransition from 'react-transition-group/CSSTransition';

import { ScrollPanel } from 'primereact/scrollpanel';

import MapComponent from './mainpanels/MapComponent';
import DataComponent from './mainpanels/DataComponent';
import UserService from '../../services/UserService';
import DataManager from './mainpanels/DataManager';
import MenuPanel from './uielements/MenuPanel';
import TopBar from './TopBar';
import './MainWindow.css';
import { toStringHDMS } from 'ol/coordinate';
import Settings from './mainpanels/Settings';


class MainWindow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            menuVisible: true,
            selectedLayers: [],
            layerStyle: 'primarystyle'
        };

        this.togglePanel = this.togglePanel.bind(this);

        this.mapReference = React.createRef();
        this.userService = new UserService();

        this.setSelectedLayers = this.setSelectedLayers.bind(this)
        this.changeLayerStyle = this.changeLayerStyle.bind(this)
    }

    componentDidMount() {
        this.fetchUsername();
    }

    fetchUsername() {
        this.userService.getUser()
            .then(response => this.setState({ username: response.data }));
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

    setSelectedLayers(ids) {
        this.setState({
            selectedLayers: ids,
        })
    }

    changeLayerStyle() {
        let nextStyle;
        if (this.state.layerStyle == "primarystyle") {
            nextStyle = 'secondarystyle';
        } else {
            nextStyle = 'primarystyle';
        }

        this.setState({ layerStyle: nextStyle }, callback => {
            if (this.mapReference.current != null) {
                this.mapReference.current.updateLayers();
            }
        })
    }

    render() {
        return (
            <div className="mainWindow">
                <div className="p-grid p-nogutter">
                    <CSSTransition in={this.state.menuVisible} appear={true} timeout={500} classNames="menuslide" onEntered={() => this.tryMapSizeUpdate()}
                        onExited={() => this.tryMapSizeUpdate()}>
                        <MenuPanel changeStyle={this.changeLayerStyle} signOut={this.props.signOut} />
                    </CSSTransition>
                    <div className="p-col main-window">
                        <div className="p-grid p-nogutter">
                            <div className="p-col-12" style={{ height: '50px' }}>
                                {/* main bar */}
                                <TopBar togglePanel={this.togglePanel} signOut={this.props.signOut} />
                            </div>
                            {/* main panel */}
                            <Router>
                                <div className="p-col-12" style={{ height: 'calc(100vh - 50px)' }}>
                                    <Route exact path="/" render={() => {
                                        return (
                                            <MapComponent layerStyle={this.state.layerStyle} loadingService={this.props.loadingService} messageService={this.props.messageService} ref={this.mapReference} layers={this.state.selectedLayers} />
                                        )
                                    }} />
                                    <Route path="/select" render={() => {
                                        return (
                                            <DataComponent setSelectedLayers={this.setSelectedLayers} />
                                        )
                                    }} />
                                    <Route path="/mydata" render={() => {
                                        return (
                                            <DataManager loadingService={this.props.loadingService} messageService={this.props.messageService} />
                                        )
                                    }} />
                                    <Route path="/settings" render={() => {
                                        return (
                                            <Settings loadingService={this.props.loadingService} messageService={this.props.messageService} />
                                        )
                                    }} />
                                </div>
                            </Router>
                        </div>
                    </div>
                </div>
            </div >
        );
    }
}

export default MainWindow;