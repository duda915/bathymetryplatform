import React, { Component } from 'react';

import { Toolbar } from 'primereact/toolbar';
import { Button } from 'primereact/button';
import UserService from '../services/UserService';

export default class TopBar extends Component {
    constructor(props) {
        super(props);

        this.state = {
            user: ''
        }

        this.userService = new UserService();
    }
    componentDidMount() {
        this.userService.getUser()
        .then(response => this.setState({user: response.data}));
    }

    render() {
        return (
            <Toolbar className="toolbar-topbar" >
                <div className="p-toolbar-group-left">
                    <Button icon="pi pi-bars" onClick={this.props.togglePanel} />
                </div>
                <div className="p-toolbar-group-right">
                <i className="pi pi-user p-toolbar-separator" style={{marginRight: '0'}}></i>
                <span className="p-toolbar-separator" style={{marginLeft: '0'}}>{this.state.user}</span>
                    <Button icon="pi pi-sign-out" onClick={this.props.logoutFun} />
                </div>
            </Toolbar>
        );
    }
}