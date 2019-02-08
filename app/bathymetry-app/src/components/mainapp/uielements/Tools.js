
import React from 'react';
import { Toolbar } from 'primereact/toolbar';
import UserService from '../../../services/UserService';
import './Tools.scss';
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
                <div className="p-col-12">
                    <div className="p-grid p-nogutter tools-username-box">
                        <div className="p-col-5" style={{'textAlign': 'right'}}>
                            <i className="pi pi-user"></i>
                        </div>
                        <div className="p-col-1 tools-username-font">
                            {this.state.username}
                        </div>
                    </div>
                </div>

                <div className="p-col-12">
                    <Toolbar className="tools-toolbar">
                        <div className="p-toolbar-group-left" >
                            <Button  style={{'marginRight': '10px'}} className="p-button-warning" onClick={this.props.signOut} icon="pi pi-sign-out" />
                            <Button className="p-button-warning" onClick={this.props.changeStyle} icon="pi pi-eye" />
                        </div>
                    </Toolbar>
                </div>
            </div>
        )
    }
}