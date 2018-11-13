import React, { Component } from 'react';
import Navbar from 'react-bootstrap/lib/Navbar';
import Cookies from 'universal-cookie';
import {RestFetch} from './utility/Rest';
import Button from 'react-bootstrap/lib/Button';

class MainWindow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
        };

        this.handleLogout = this.handleLogout.bind(this);
    }

    componentDidMount() {
        this.fetchUsername();
    }

    fetchUsername() {
        let cookie = new Cookies();

        fetch(RestFetch.logged, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + cookie.get("access_token")
            }
        }).then(res => {
            console.log('login: ' + res.status);
            if(res.status === 200) {
                res.text().then(response => this.setState({username: response}));
            }
        });
    }

    handleLogout() {
        let cookie = new Cookies();

        fetch(RestFetch.logout, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + cookie.get("access_token")
            }
        }).then(res => {
            cookie.remove("access_token", {path: '/'});
            cookie.remove("refresh_token", {path: '/'});
            this.props.changeLoginState(false);
        });
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