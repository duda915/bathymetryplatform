import React, { Component } from 'react';
import Navbar from 'react-bootstrap/lib/Navbar';
import {RestFetch} from './utility/Rest';
import Button from 'react-bootstrap/lib/Button';
import Row from 'react-bootstrap/lib/Row';
import Col from'react-bootstrap/lib/Col';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import TileWMS from 'ol/source/TileWMS.js';
import MapMenu from './MapMenu';

class MainWindow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            mapResizedCols: 10,
            mapCols: 12,
            isPanelVisible: false
        };

        this.map = null;
        this.layer = null;
        this.handleLogout = this.handleLogout.bind(this);
        this.togglePanel = this.togglePanel.bind(this);
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
        let baseLayer = [
            new TileLayer({
                source: new XYZ({
                    url: 'https://{a-c}.tile.openstreetmap.org/{z}/{x}/{y}.png'
                  })
            })
          ];

        
        let wmsSource = new TileWMS({
            url: RestFetch.geoserver,
            params: {'LAYERS': 'bathymetry:bathymetry', 'TILED': true, 'viewparams': 'selection:20\\,21', 'width': '200', 'height': '200'},
            serverType: 'geoserver',
            transition: 0
          }) 

        this.layer = new TileLayer({
            source: wmsSource
        });

        let view = new View({
            center: [19, 51],
            zoom: 2
          });

        this.map = new Map({
            layers: baseLayer,
            target: 'map',
            view: view
        });

        this.map.addLayer(this.layer);

        this.map.on('singleclick', function(evt) {
            
            var viewResolution = /** @type {number} */ (view.getResolution());
            var url = wmsSource.getGetFeatureInfoUrl(
              evt.coordinate, viewResolution, 'EPSG:3857',
              {'INFO_FORMAT': 'application/json'});
            if (url) {
              console.log(url);
            }
        });
    }

    togglePanel() {
        let mapTargetCols = 12;
        if(!this.state.isPanelVisible) {
            mapTargetCols = this.state.mapResizedCols;
        }

        this.setState({
            isPanelVisible: !this.state.isPanelVisible,
            mapCols: mapTargetCols
        }, callback => this.map.updateSize());

    }

    render() {
        return (
            <div className="mainWindow">
                <Navbar bg="dark" variant="dark">
                <Button variant="primary" onClick={this.togglePanel}>Menu</Button>
                    <Navbar.Brand>Bathymetry Platform</Navbar.Brand>
                    <Navbar.Toggle />
                    <Navbar.Collapse className="justify-content-end">
                        <Navbar.Text className="p-2">
                            Logged in as: <b>{this.state.username}</b>
                        </Navbar.Text>
                        <Button variant="primary" onClick={this.handleLogout}>Logout</Button>
                    </Navbar.Collapse>
                </Navbar>
                <div className="container-fluid w-100" style={{height: 'calc(100vh - 56px)'}}>
                    <Row className='h-100'>
                        {this.state.isPanelVisible ? (
                            <Col xs={2} className='h-100 p-0'>
                                <MapMenu></MapMenu>
                            </Col>
                        ) : (null)}
                        <Col xs={this.state.mapCols} className='h-100 p-0'>
                            <div id="map" className="h-100">
                            </div>
                        </Col>
                    </Row>
                </div>
            </div>
        );
    }
} 

export default MainWindow;