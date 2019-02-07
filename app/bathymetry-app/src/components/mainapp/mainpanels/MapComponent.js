import React, { Component } from 'react';

import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import TileWMS from 'ol/source/TileWMS.js';
import {transform} from 'ol/proj.js'
import ServiceMeta from '../../../services/ServiceMeta';
import { DragBox, Select } from 'ol/interaction.js';
import {platformModifierKeyOnly} from 'ol/events/condition.js';
import {transformExtent} from 'ol/proj.js';
import {Dialog} from 'primereact/dialog';
import {Button} from 'primereact/button';
import downloadjs from 'downloadjs';
import LoadingComponent from '../../utility/LoadingComponent';
import DataService from '../../../services/DataService';
import GeoServerService from '../../../services/GeoServerService';

import BoundingBoxDTO from '../../../services/dtos/BoundingBoxDTO';
import CoordinateDTO from '../../../services/dtos/CoordinateDTO';


export default class MapComponent extends Component {
    constructor(props) {
        super(props);

        this.map = null;
        this.layer = null;
        this.wmsSource = null;
        this.olOnClickFunction = null;
        this.olView = null;

        this.olGenerateGetFeatureInfoFunction = this.olGenerateGetFeatureInfoFunction.bind(this);
        this.hideDialog = this.hideDialog.bind(this);
        this.downloadAccept = this.downloadAccept.bind(this);
        this.serviceMeta = new ServiceMeta();
        this.dataService = new DataService();
        this.geoServerService = new GeoServerService();

        this.state = {
            downloadDialog: false,
            selectionRecords: 0,
        }
    }

    componentDidMount() {
        this.initOpenLayers();
        this.loadLayer(this.props.layers);
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
            projection: 'EPSG:3857',
            center: [19, 51],
            zoom: 2,
        });

        this.map = new Map({
            layers: baseLayer,
            target: 'map',
            view: this.olView
        });

        let select = new Select();
        this.map.addInteraction(select);

        let dragBox = new DragBox({
            condition: platformModifierKeyOnly
        });

        this.map.addInteraction(dragBox);
        dragBox.on('boxend', function () {
            // features that intersect the box are added to the collection of
            // selected features


            let extent = dragBox.getGeometry().getExtent();
            let transformed = transformExtent(extent, 'EPSG:3857', 'EPSG:4326');

            
            const upperLeftCoord = new CoordinateDTO(transformed[0], transformed[3]);
            const lowerLeftCoord = new CoordinateDTO(transformed[2], transformed[1]);
            const box = new BoundingBoxDTO(upperLeftCoord, lowerLeftCoord);

            console.log(box);
            this.setState({box: box});
            console.log(this.props.layers);


            if(this.props.layers.length == 0) {
                return;
            }

            this.props.loadingService(true);

            this.dataService.getSelectionDataSetCount(this.props.layers, box)
            .then(response => {
                if(response.data.response == 0) {
                    this.props.messageService('info', 'No data', 'No data found within polygon');
                } else {
                    this.setState({selectionRecords: response.data.response}, callback => {
                        this.setState({downloadDialog: true});
                    })
                }
            })
            .finally(e => this.props.loadingService(false));
        }.bind(this));

    }

    downloadAccept() {
        this.props.loadingService(true);
        this.setState({selectionData: null, downloadDialog: false, selectionRecords: 0});
        this.dataService.downloadSelectedDataSets(this.props.layers, this.state.box)
        .finally(e=> this.props.loadingService(false));
    }
    
    hideDialog() {
        this.setState({selectionData:null, downloadDialog: false, selectionRecords: 0})
    }

    loadLayer(layersId) {
        if(layersId.length === 0) {
            return;
        }

        

        let layers = 'bathymetry:' + layersId[0];

        for (let i = 1; i < layersId.length; i++) {
            layers += ",bathymetry:" + layersId[i];
        }

        let wmsParams = {
            'LAYERS': layers,
            'TILED': true,
            // 'viewparams': selection,
            // 'width': '200',
            // 'height': '200',
            // 'format': 'image/png8'
        };

        this.prepareLayerChange();
        this.wmsSource = new TileWMS({
            url: this.serviceMeta.getGeoServerAddress(),
            params: wmsParams,
            serverType: 'geoserver',
            transition: 0,
            projection: 'EPSG:3857'
        })

        this.layer = new TileLayer({
            source: this.wmsSource
        });
        this.map.addLayer(this.layer);

        this.dataService.getLayerCenter(layersId[0])
        .then(response => {
            let coord = [response.data.x, response.data.y];
            let reprojected = transform(coord, 'EPSG:4326', 'EPSG:3857');

            this.olView = new View ({
                projection: 'EPSG:3857',
                center: reprojected,
                zoom: 10,
            });
            
            this.map.setView(this.olView);
            this.olOnClickFunction = this.olGenerateGetFeatureInfoFunction;
            this.map.on('singleclick', this.olOnClickFunction);
        });
        
    }

    prepareLayerChange() {
        this.map.removeLayer(this.layer);
        this.map.un('singleclick', this.olOnClickFunction);
    }

    olGenerateGetFeatureInfoFunction(evt) {
        let viewResolution = this.olView.getResolution();
        let url = this.wmsSource.getGetFeatureInfoUrl(
            evt.coordinate, viewResolution, 'EPSG:3857',
            { 'INFO_FORMAT': 'application/json' });
        if (url) {
            this.geoServerService.geoserverGetFeatureInfo(url)
            .then(response => {
                console.log(response.data.features);
                let features = response.data.features;

                if(features.length === 0) {
                    return;
                }

                console.log(features[0]);
                // let info = "lat: " + features[0].geometry.coordinates[0] + " long: " + features[0].geometry.coordinates[1]
                //     + " measurement: " + features[0].properties.measure;
                let info = "measurement: " + features[0].properties.GRAY_INDEX;
                this.props.messageService('info', 'Point', info);
            });
        }
    }

    updateMapSize() {
        this.map.updateSize();
    }

    render() {
        const dialogFooter = (
            <div>
                <Button label="Download" icon="pi pi-check" onClick={this.downloadAccept} />
                <Button label="Cancel" icon="pi pi-times" onClick={this.hideDialog} />
            </div>
        );

        return (
            <div className="mapComponent" style={{ height: '100%' }}>
            <LoadingComponent ref={(ref) => this.progress = ref}/>
            <Dialog header="Download selection" footer={dialogFooter} visible={this.state.downloadDialog} width="350px" modal={true} onHide={this.hideDialog}>
                Found {this.state.selectionRecords} records.
            </Dialog>
                <div id="map" style={{ height: '100%' }}></div>
            </div>
        );
    }




}