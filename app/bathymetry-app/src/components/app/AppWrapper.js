import LayerGroup from "ol/layer/Group";
import { default as TileLayer } from "ol/layer/Tile";
import TileWMS from "ol/source/TileWMS.js";
import React, { Component } from "react";
import { HashRouter as Router, Route } from "react-router-dom";
import { geoServerAPI } from "../../services/ServiceMetaData";
import "./AppWrapper.css";
import DataComponent from "./datachooser/DataComponent";
import DataManager from "./datamanager/DataManager";
import MapComponent from "./map/MapComponent";
import MenuPanel from "./sidemenu/MenuPanel";
import Settings from "./usersettings/Settings";

class AppWrapper extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedLayers: [],
      layerStyle: "primarystyle",
      selectedLayersGroup: this.buildLayersGroup()
    };

    this.mapReference = React.createRef();
  }

  buildLayersGroup() {
    const layersGroup = new LayerGroup({
      title: "Selected layers",
      layers: []
    });

    return layersGroup;
  }

  zoomToLayer = layerId => {
    if (this.mapReference.current != null) {
      this.mapReference.current.zoomToLayer(layerId);
    }
  };

  zoomFit = () => {
    if (this.mapReference.current != null) {
      this.mapReference.current.zoomFit();
    }
  };

  tryMapUpdate() {
    if (this.mapReference.current != null) {
      this.mapReference.current.updateLayerGroup();
    }
  }

  setSelectedLayers = ids => {
    const layersGroup = this.buildLayersGroup();
    ids.forEach(element => {
      const newLayer = this.createLayer(element.id);
      layersGroup.getLayers().push(newLayer);
    });
    this.setState({
      selectedLayers: ids,
      selectedLayersGroup: layersGroup
    });
  };

  createLayer(layer) {
    const wmsParams = {
      LAYERS: `bathymetry:${layer}`,
      TILED: true,
      STYLES: this.state.layerStyle
    };

    const wmsSource = new TileWMS({
      url: geoServerAPI,
      params: wmsParams,
      serverType: "geoserver",
      transition: 0,
      projection: "EPSG:3857"
    });

    let newLayer = new TileLayer({
      title: layer,
      source: wmsSource
    });

    return newLayer;
  }

  toggleLayer = (layer, visible) => {
    this.state.selectedLayersGroup.getLayers().forEach(l => {
      if (l.get("title") === layer) {
        l.setVisible(visible);
      }
    });

    this.setState({
      selectedLayers: this.state.selectedLayers.map(sl => {
        if (sl.id === layer) {
          sl.visible = visible;
        }

        return sl;
      })
    });
  };

  changeLayerStyle = () => {
    let nextStyle;
    if (this.state.layerStyle === "primarystyle") {
      nextStyle = "secondarystyle";
    } else {
      nextStyle = "primarystyle";
    }

    this.setState({ layerStyle: nextStyle }, callback => {
      this.state.selectedLayersGroup.getLayers().forEach(layer => {
        const params = layer.getSource().getParams();
        params.STYLES = this.state.layerStyle;
        layer.getSource().updateParams(params);
      });
    });
  };

  render() {
    return (
      <div>
        <div className="p-grid p-nogutter">
          <div className="p-col-12 p-lg-3">
            <MenuPanel
              changeStyle={this.changeLayerStyle}
              signOut={this.props.signOut}
              selectedLayers={this.state.selectedLayers}
              toggleLayer={this.toggleLayer}
              zoomToLayer={this.zoomToLayer}
              zoomFit={this.zoomFit}
            />
          </div>
          <Router>
            <div className="p-col-12 p-lg-9">
              <Route
                exact
                path="/"
                render={() => {
                  return (
                    <MapComponent
                      layerStyle={this.state.layerStyle}
                      loadingService={this.props.loadingService}
                      messageService={this.props.messageService}
                      ref={this.mapReference}
                      layers={this.state.selectedLayers}
                      layersGroup={this.state.selectedLayersGroup}
                    />
                  );
                }}
              />
              <Route
                path="/select"
                render={() => {
                  return (
                    <DataComponent
                      setSelectedLayers={this.setSelectedLayers}
                      loadingService={this.props.loadingService}
                      messageService={this.props.messageService}
                    />
                  );
                }}
              />
              <Route
                path="/mydata"
                render={() => {
                  return (
                    <DataManager
                      loadingService={this.props.loadingService}
                      messageService={this.props.messageService}
                    />
                  );
                }}
              />
              <Route
                path="/settings"
                render={() => {
                  return (
                    <Settings
                      loadingService={this.props.loadingService}
                      messageService={this.props.messageService}
                    />
                  );
                }}
              />
            </div>
          </Router>
        </div>
      </div>
    );
  }
}

export default AppWrapper;
