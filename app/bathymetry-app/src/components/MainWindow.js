import React, { Component } from 'react';
import CSSTransition from 'react-transition-group/CSSTransition';

import {RestFetch} from './utility/Rest';

import 'primereact/resources/primereact.min.css';
import 'primereact/resources/themes/nova-colored/theme.css';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';

import {Toolbar} from 'primereact/toolbar';
import {Button} from 'primereact/button';
import {ScrollPanel} from 'primereact/scrollpanel';

import MapComponent from './mainpanels/MapComponent';

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

    }

    componentDidMount() {
        this.fetchUsername();
    }

    fetchUsername() {
        let setusername = (function (username) {
            this.setState({
                username: username
            })
        }).bind(this);

        RestFetch.getUsername(null, setusername);
    }
    
    handleLogout() {
        RestFetch.sendLogout(this.props.changeLoginState.bind(null, false));
    }

    togglePanel() {
        this.setState({
            menuVisible: !this.state.menuVisible,
        });

    }

    tryMapSizeUpdate() {
        // this.mapRefreshRef = React.createRef();
        if(this.mapRefreshRef != null) {
            this.mapRefreshRef.current.updateMapSize();
        }
    }

    render() {
        return (
            <div className="mainWindow">
                        {/* main bar */}
                        <Toolbar className="toolbar-topbar" style={{height: '50px'}}>
                            <div className="p-toolbar-group-left">
                                <Button icon="pi pi-bars" onClick={this.togglePanel}/>
                            </div>
                            <div className="p-toolbar-group-right">
                                <Button icon="pi pi-sign-out" onClick={this.handleLogout}/>
                            </div>
                        </Toolbar>

                        <div className="p-grid p-nogutter" style={{width: '100%', height: 'calc(100vh - 50px)'}}> 
                            {/* left menu */}
                            <CSSTransition in={this.state.menuVisible} appear={true} timeout={500} classNames="menuslide" onEntered={() => this.tryMapSizeUpdate()}
                            onExited={() => this.tryMapSizeUpdate()}>
                                <ScrollPanel className="p-col-fixed menuslide-init">

                                </ScrollPanel>
                            </CSSTransition>

                            {/* main panel */}
                            <div className="p-col">
                                <MapComponent ref={this.mapRefreshRef}/>
                            </div>
                        </div>

                </div>

            // </div>
        );
    }
} 

export default MainWindow;