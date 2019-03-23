import { platformModifierKeyOnly } from "ol/events/condition.js";
import { boundingExtent } from "ol/extent.js";
import { DragBox } from "ol/interaction.js";
import LayerGroup from "ol/layer/Group";
import { default as LayerTile, default as TileLayer } from "ol/layer/Tile";
import Map from "ol/Map";
import { transform, transformExtent } from "ol/proj.js";
import SourceOSM from "ol/source/OSM";
import View from "ol/View";

export class BathymetryMap {
  constructor() {
    this._view = this._initializers().initializeView();
    this._bathymetryLayers = this._initializers().initializeLayers();
    this._map = this._initializers().initializeMap();
  }
  
  getView() {
    return this._view;
  }

  removeLayers = () => {
    this._bathymetryLayers.getLayers().clear();
  }

  toggleLayer = (name, visibility) => {
    this._bathymetryLayers.getLayers().forEach(layer => {
      if (layer.get("title") === name) {
        layer.setVisible(visibility);
      }
    });
  };

  zoomToExtent = extent => {
    this._map.getView().fit(extent);
  };

  addLayer = (name, wmsSource) => {
    const layer = new TileLayer({
      title: name,
      source: wmsSource
    });

    this._bathymetryLayers.getLayers().push(layer);
  };

  addOnClickInteraction = interaction => {
    this._map.on("singleclick", interaction);
  };

  addDragBoxInteraction = interaction => {
    const dragBox = new DragBox({
      condition: platformModifierKeyOnly
    });

    dragBox.on("boxend", interaction);

    this._map.addInteraction(dragBox);
  };

  _initializers = () => {
    const initializers = {
      initializeView: () =>
        new View({
          projection: "EPSG:3857",
          center: [19, 51],
          zoom: 2
        }),

      initializeLayers: () =>
        new LayerGroup({
          title: "Selected layers",
          layers: []
        }),

      initializeMap: () =>
        new Map({
          target: "map",
          layers: [
            new LayerGroup({
              title: "Base maps",
              layers: [
                new LayerTile({
                  title: "OSM",
                  type: "base",
                  visible: true,
                  source: new SourceOSM()
                })
              ]
            }),
            this._bathymetryLayers
          ],
          view: this._view
        })
    };

    return initializers;
  };
}
