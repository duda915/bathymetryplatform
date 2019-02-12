import React, { Component } from "react";
import { HashRouter as Router, Route } from "react-router-dom";
import CSSTransition from "react-transition-group/CSSTransition";


import { ScrollPanel } from "primereact/scrollpanel";

import MapComponent from "./mainpanels/MapComponent";
import DataComponent from "./mainpanels/DataComponent";
import UserService from "../../services/UserService";
import DataManager from "./mainpanels/DataManager";
import MenuPanel from "./uielements/MenuPanel";
import TopBar from "./TopBar";
import "./MainWindow.css";
import { toStringHDMS } from "ol/coordinate";
import Settings from "./mainpanels/Settings";
import LayerGroup from "ol/layer/Group";
import TileWMS from "ol/source/TileWMS.js";
import { default as LayerTile, default as TileLayer } from "ol/layer/Tile";
import ServiceMeta from "../../services/ServiceMeta";

class MainWindow extends Component {
  constructor(props) {
    super(props);

    this.state = {
      username: "",
      menuVisible: true,
      selectedLayers: [],
      layerStyle: "primarystyle",
      selectedLayersGroup: this.buildLayersGroup()
    };

    this.togglePanel = this.togglePanel.bind(this);

    this.mapReference = React.createRef();
    this.userService = new UserService();
    this.serviceMeta = new ServiceMeta();

    this.setSelectedLayers = this.setSelectedLayers.bind(this);
    this.changeLayerStyle = this.changeLayerStyle.bind(this);
    this.toggleLayer = this.toggleLayer.bind(this)
  }

  buildLayersGroup() {
    const layersGroup = new LayerGroup({
      title: "Selected layers",
      layers: []
    });

    return layersGroup;
  }

  componentDidMount() {
    this.fetchUsername();
  }

  fetchUsername() {
    this.userService
      .getUser()
      .then(response => this.setState({ username: response.data }));
  }

  togglePanel() {
    this.setState({
      menuVisible: !this.state.menuVisible
    });
  }

  tryMapSizeUpdate() {
    if (this.mapReference.current != null) {
      this.mapReference.current.updateMapSize();
    }
  }

  tryMapUpdate() {
    if (this.mapReference.current != null) {
      this.mapReference.current.updateLayerGroup();
    }
  }

  setSelectedLayers(ids) {
    const layersGroup = this.buildLayersGroup();
    ids.forEach(element => {
      const newLayer = this.createLayer(element.id);
      layersGroup.getLayers().push(newLayer);
    });
    console.log(this.state.layerStyle);
    this.setState({
      selectedLayers: ids,
      selectedLayersGroup: layersGroup
    });
  }

  createLayer(layer) {
    const wmsParams = {
      LAYERS: `bathymetry:${layer}`,
      TILED: true,
      STYLES: this.state.layerStyle
    };

    const wmsSource = new TileWMS({
      url: this.serviceMeta.getGeoServerAddress(),
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

  toggleLayer(layer, visible) {
    console.log(layer);
    console.log(visible);
    this.state.selectedLayersGroup.getLayers().forEach(l => {
      if (l.get("title") === layer) {
        l.setVisible(visible);
      }
    });

    const selectedLayers = this.state.selectedLayers.map(l => {
      if(l.id == layer) {
        console.log('x')
        l.visible = visible
      }
      return l
    })

    this.setState({
      selectedLayers: this.state.selectedLayers.map(sl => {
        if(sl.id === layer) {
          console.log('xxx')
          sl.visible = visible
        }

        return sl;
      })
    });

  }

  changeLayerStyle() {
    let nextStyle;
    if (this.state.layerStyle == "primarystyle") {
      nextStyle = "secondarystyle";
    } else {
      nextStyle = "primarystyle";
    }

    this.setState({ layerStyle: nextStyle }, callback => {
      this.state.selectedLayersGroup.getLayers().forEach(layer => {
        console.log(layer.get("title"));
        const params = layer.getSource().getParams();
        params.STYLES = this.state.layerStyle;
        layer.getSource().updateParams(params);
      });
    });
  }

  render() {
    return (
      <div className="mainWindow">
        <div className="p-grid p-nogutter">
          <CSSTransition
            in={this.state.menuVisible}
            appear={true}
            timeout={500}
            classNames="menuslide"
            onEntered={() => this.tryMapSizeUpdate()}
            onExited={() => this.tryMapSizeUpdate()}
          >
            <MenuPanel
              changeStyle={this.changeLayerStyle}
              signOut={this.props.signOut}
              selectedLayers={this.state.selectedLayers}
              toggleLayer={this.toggleLayer}
            />
          </CSSTransition>
          <div className="p-col main-window">
            <div className="p-grid p-nogutter">
              {/* <div className="p-col-12" style={{ height: '50px' }}>
                                <TopBar togglePanel={this.togglePanel} signOut={this.props.signOut} />
                            </div> */}
              {/* main panel */}
              <Router>
                <div className="p-col-12" style={{ height: "calc(100vh)" }}>
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
        </div>
      </div>
    );
  }
}

export default MainWindow;
