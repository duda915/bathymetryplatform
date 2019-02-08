
import React from 'react';
import { Toolbar } from 'primereact/toolbar';
import UserService from '../../../services/UserService';
import './Tools.css';
import { Button } from 'primereact/button';

export default class Tools extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            username: ''
        }

        this.userService = new UserService();

    }

    componentDidMount() {
        this.fetchUsername();
    }

    fetchUsername() {
        this.userService.getUser()
            .then(response => this.setState({ username: response.data.username }))
    }

    render() {
        return (
            <div className="p-grid p-nogutter tools-username">
                <div className="p-col-1">
                    <i className="pi pi-user"></i>
                </div>
                <div className="p-col-11">
                    {this.state.username}
                </div>
                <div className="p-col-12">
                    <Toolbar className="tools-toolbar">
                        <div className="p-toolbar-group-right" >
                            <Button className="p-button-info" onClick={this.props.changeStyle} icon="pi pi-eye"/>
                        </div>
                        <div className="p-toolbar-group-left" >
                            <Button className="p-button-info" onClick={this.props.signOut} icon="pi pi-sign-out"/>
                        </div>
                    </Toolbar>
                </div>
            </div>
        )
    }
}