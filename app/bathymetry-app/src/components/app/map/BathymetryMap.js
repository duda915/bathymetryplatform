import { platformModifierKeyOnly } from "ol/events/condition.js";
import { boundingExtent } from "ol/extent.js";
import { DragBox } from "ol/interaction.js";
import LayerGroup from "ol/layer/Group";
import { default as LayerTile, default as TileLayer } from "ol/layer/Tile";
import Map from "ol/Map";
import { transform, transformExtent } from "ol/proj.js";
import SourceOSM from "ol/source/OSM";
import TileWMS from "ol/source/TileWMS.js";
import View from "ol/View";

export default class BathymetryMap {
  constructor() {
    this.view = this._initializers().initializeView();
    this.bathymetryLayers = this._initializers().initializeLayers();
    this.map = this._initializers().initializeMap();
  }

  toggleLayer = (name, visibility) => {
    this.bathymetryLayers.getLayers().forEach(layer => {
      if (layer.get("title") === name) {
        layer.setVisible(visibility);
      }
    });
  };

  zoomToExtent = extent => {
    this.map.getView().fit(extent);
  };

  addLayer = (name, wmsSource) => {
    const layer = new TileLayer({
      title: name,
      source: wmsSource
    });

    this.bathymetryLayers.getLayers().push(layer);
  };

  addOnClickInteraction = interaction => {
    this.map.on("singleclick", interaction);
  };

  addDragBoxInteraction = interaction => {
    const dragBox = new DragBox({
      condition: platformModifierKeyOnly
    });

    dragBox.on("boxend", interaction);

    this.map.addInteraction(dragBox);
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
            this.bathymetryLayers
          ],
          view: this.view
        })
    };

    return initializers;
  };
}
