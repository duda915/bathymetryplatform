import React, { Component } from 'react';
import Navbar from 'react-bootstrap/lib/Navbar';
import {RestFetch} from './utility/Rest';
import Button from 'react-bootstrap/lib/Button';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import TileWMS from 'ol/source/TileWMS.js';

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
        this.initOpenLayers();
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

    initOpenLayers() {
        var layers = [
            new TileLayer({
                source: new XYZ({
                    url: 'https://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png'
                  })
            }),
            new TileLayer({
              source: new TileWMS({
                url: RestFetch.geoserver,
                params: {'LAYERS': 'bathymetry:bathymetry', 'TILED': true, 'viewparams': 'selection:21\\,15'},
                serverType: 'geoserver',
                transition: 0
              })
            })
          ];

        new Map({
            layers: layers,
            target: 'map',
            view: new View({
              center: [19, 51],
              zoom: 2
            })
          });
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
                    </div>
                </div>
            </div>
        );
    }
} 

export default MainWindow;