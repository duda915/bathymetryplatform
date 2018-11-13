import React, { Component } from 'react';
import Navbar from 'react-bootstrap/lib/Navbar';
import {RestFetch} from './utility/Rest';
import Button from 'react-bootstrap/lib/Button';

class MainWindow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
        };

        this.handleLogout = this.handleLogout.bind(this);
        // this.setUsername = this.setUsername.bind(this);
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

    render() {
        return (
            <Navbar bg="dark" variant="dark">
                <Navbar.Brand>Bathymetry Platform</Navbar.Brand>
                <Navbar.Toggle />
                <Navbar.Collapse className="justify-content-end">
                    <Navbar.Text className="p-2">
                        Logged in as: <b>{this.state.username}</b>
                    </Navbar.Text>
                    <Button variant="primary" onClick={this.handleLogout}>Logout</Button>
                </Navbar.Collapse>
            </Navbar>
        );
    }
} 

export default MainWindow;