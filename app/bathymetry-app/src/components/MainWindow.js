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
import DataModal from './DataModal';

class MainWindow extends Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            mapResizedCols: 9,
            mapCols: 12,
            isPanelVisible: false,
            showInfo: false,
            measure: null,
        };

        this.map = null;
        this.layer = null;
        this.wmsSource = null;
        this.olOnClickFunction = null;
        this.olView = null;

        this.handleLogout = this.handleLogout.bind(this);
        this.togglePanel = this.togglePanel.bind(this);
        this.loadLayer = this.loadLayer.bind(this);
        this.olGenerateGetFeatureInfoFunction = this.olGenerateGetFeatureInfoFunction.bind(this); 
        this.showFeatureInfo = this.showFeatureInfo.bind(this);
        this.closeFeatureInfo = this.closeFeatureInfo.bind(this);
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

        this.olView = new View({
            center: [19, 51],
            zoom: 2
          });

        this.map = new Map({
            layers: baseLayer,
            target: 'map',
            view: this.olView
        });

        // this.map.on('singleclick', function(evt) {
            
        //     var viewResolution = /** @type {number} */ (view.getResolution());
        //     var url = wmsSource.getGetFeatureInfoUrl(
        //       evt.coordinate, viewResolution, 'EPSG:3857',
        //       {'INFO_FORMAT': 'application/json'});
        //     if (url) {
        //       console.log(url);
        //     }
        // });
    }

    loadLayer(id) {
        this.prepareLayerChange();
        this.wmsSource = new TileWMS({
            url: RestFetch.geoserver,
            params: {'LAYERS': 'bathymetry:bathymetry', 'TILED': true, 'viewparams': 'selection:'+id, 'width': '200', 'height': '200'},
            serverType: 'geoserver',
            transition: 0
          }) 

        this.layer = new TileLayer({
            source: this.wmsSource
        });
        this.map.addLayer(this.layer);

        this.olOnClickFunction = this.olGenerateGetFeatureInfoFunction;
        this.map.on('singleclick', this.olOnClickFunction);
    }

    prepareLayerChange() {
        this.map.removeLayer(this.layer);
        this.map.un('singleclick', this.olOnClickFunction);
    }

    olGenerateGetFeatureInfoFunction(evt) {
        let viewResolution = /** @type {number} */ (this.olView.getResolution());
        let url = this.wmsSource.getGetFeatureInfoUrl(
            evt.coordinate, viewResolution, 'EPSG:3857',
            {'INFO_FORMAT': 'application/json'});
        if (url) {
            console.log(url);
            fetch(url).then(response => response.json().then(json => {
                if(json.features.length !== 0) {
                    this.showFeatureInfo(json.features[0].properties.measure)
                }
            }));
        }
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

    showFeatureInfo(measure) {
        this.setState({
            showInfo: true,
            measure: measure
        });
    }

    closeFeatureInfo() {
        this.setState({
            showInfo: false
        })
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
                <DataModal show={this.state.showInfo} measure={this.state.measure} close={this.closeFeatureInfo}></DataModal>
                <div className="container-fluid w-100" style={{height: 'calc(100vh - 56px)'}}>
                    <Row className='h-100'>
                        {this.state.isPanelVisible ? (
                            <Col xs={3} className='h-100 p-0'>
                                <MapMenu loadLayer={this.loadLayer}></MapMenu>
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