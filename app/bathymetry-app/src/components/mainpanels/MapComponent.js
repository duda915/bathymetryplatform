import React, { Component } from 'react';

import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import TileWMS from 'ol/source/TileWMS.js';
import ServiceMeta from '../../services/ServiceMeta';
import { DragBox, Select } from 'ol/interaction.js';
import {platformModifierKeyOnly} from 'ol/events/condition.js';
import {transformExtent} from 'ol/proj.js';
import DataService from '../../services/DataService';
import {Growl} from 'primereact/growl';
import {Dialog} from 'primereact/dialog';
import {Button} from 'primereact/button';
import downloadjs from 'downloadjs';
import LoadingComponent from '../utility/LoadingComponent';

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

            this.progress.showProgress(true);

            let extent = dragBox.getGeometry().getExtent();
            let transformed = transformExtent(extent, 'EPSG:3857', 'EPSG:4326');
            console.log(extent);
            console.log(transformed);
            this.dataService.downloadSelectedDataSets(this.props.layers, transformed)
            .then(response => {
                console.log(response);
                let records = response.data.split(/\r\n|\r|\n/).length - 2;
                this.setState({selectionData: response.data, selectionRecords: records}, callback => this.setState({downloadDialog: true}))
            })
            .catch(error => {
                console.log(error.response)
                this.growl.show({severity: 'info', summary: 'No data', detail: 'No data found within polygon'})
            })
            .finally(e => this.progress.showProgress(false));
        }.bind(this));

    }

    downloadAccept() {
        downloadjs(this.state.selectionData, "bathymetry_selection.csv", "text/plain");
        this.setState({selectionData: null, downloadDialog: false, selectionRecords: 0});
    }
    
    hideDialog() {
        this.setState({selectionData:null, downloadDialog: false, selectionRecords: 0})
    }

    loadLayer(layersId) {
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
            url: this.serviceMeta.getGeoServerServiceAddress(),
            params: wmsParams,
            serverType: 'geoserver',
            transition: 0,
            projection: 'EPSG:3857'
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
        let viewResolution = this.olView.getResolution();
        let url = this.wmsSource.getGetFeatureInfoUrl(
            evt.coordinate, viewResolution, 'EPSG:3857',
            { 'INFO_FORMAT': 'application/json' });
        if (url) {
            this.dataService.geoserverGetFeatureInfo(url)
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
                this.growl.show({severity: 'info', summary: 'Point', detail: info})
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
                <Growl ref={(el) => this.growl = el} />
                <div id="map" style={{ height: '100%' }}></div>
            </div>
        );
    }




}