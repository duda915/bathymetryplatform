import React, { Component } from 'react';

import { Toolbar } from 'primereact/toolbar';
import { Button } from 'primereact/button';

export default class TopBar extends Component {

    render() {
        return (
            <Toolbar className="toolbar-topbar" >
                <div className="p-toolbar-group-left">
                    <Button icon="pi pi-bars" onClick={this.props.togglePanel} />
                </div>
                <div className="p-toolbar-group-right">
                    <Button icon="pi pi-sign-out" onClick={this.props.logoutFun} />
                </div>
            </Toolbar>
        );
    }
}