import React, { Component } from 'react';
import {RestFetch} from '../utility/Rest';

import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import XYZ from 'ol/source/XYZ';
import TileWMS from 'ol/source/TileWMS.js';

export default class MapComponent extends Component{
    constructor(props) {
        super(props);

        this.map = null;
        this.layer = null;
        this.wmsSource = null;
        this.olOnClickFunction = null;
        this.olView = null;
    }

    componentDidMount() {
        this.initOpenLayers();
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

    updateMapSize() {
        this.map.updateSize();
    }

    render() {
        return(
            <div id="map" style={{height: '100%'}}></div>
        );
    }

    


}