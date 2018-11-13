import React, { Component } from 'react';
import Navbar from 'react-bootstrap/lib/Navbar';
import {RestFetch} from './utility/Rest';
import Button from 'react-bootstrap/lib/Button';
import L from 'leaflet';

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
        this.initLeaflet();
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

    initLeaflet() {
        let mymap = L.map('map').setView([51.9, 19.14], 7);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
        }).addTo(mymap)
    }

    render() {
        return (
            <div className="mainWindow">
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
                <div className='container-fluid p-0' style={{height: 'calc(100vh - 56px)'}}>
                    <div id="map" className='h-100'>
                    s
                    </div>
                </div>
            </div>
        );
    }
} 

export default MainWindow;