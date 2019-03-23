import {
  FETCH_FEATURE_INFO,
  MAP_DRAG_BOX,
  TOGGLE_LAYER,
  TOGGLE_STYLE,
  ZOOM_TO_FIT,
  ZOOM_TO_LAYER,
  ADD_LAYER,
  REMOVE_LAYERS
} from "./MapActions";
import { combineReducers } from "redux";

function featureInfoUrl(state = null, action) {
  switch (action.type) {
    case FETCH_FEATURE_INFO:
      return action.payload.featureInfoUrl;
    default:
      return state;
  }
}

function dragBoxCoordinates(state = {}, action) {
  switch (action.type) {
    case MAP_DRAG_BOX:
      return action.payload.box;
    default:
      return state;
  }
}

function style(state = false, action) {
  switch (action.type) {
    case TOGGLE_STYLE:
      return action.payload.requestToggleStyle;
    default:
      return state;
  }
}

function zoomToFit(state = false, action) {
  switch (action.type) {
    case ZOOM_TO_FIT:
      return action.payload.requestZoom;
    default:
      return state;
  }
}

function zoomToLayer(state = null, action) {
  switch (action.type) {
    case ZOOM_TO_LAYER:
      return action.payload.layerId;
    default:
      return state;
  }
}

function layers(state = [], action) {
  switch (action.type) {
    case ADD_LAYER:
      return [...state, action.payload.layer];
    case REMOVE_LAYERS:
      return [];
    case TOGGLE_LAYER:
      return state.map(layer => {
        if (layer.id === action.payload.layerId) {
          layer.visible = !layer.visible;
        }
      });
    default:
      return state;
  }
}

const map = combineReducers({
  featureInfoUrl,
  dragBoxCoordinates,
  style,
  zoomToFit,
  zoomToLayer,
  layers
});

export default map;
