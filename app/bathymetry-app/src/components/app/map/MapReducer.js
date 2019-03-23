import {
  SEND_MAP_COMMAND,
  TOGGLE_LAYER,
  ADD_LAYERS,
  REMOVE_LAYERS
} from "./MapActions";
import { combineReducers } from "redux";

function command(state = {}, action) {
  switch (action.type) {
    case SEND_MAP_COMMAND:
      return action.payload;
    default:
      return state;
  }
}

function layers(state = [], action) {
  switch (action.type) {
    case ADD_LAYERS:
      return [...state, ...action.payload.layers];
    case REMOVE_LAYERS:
      return [];
    case TOGGLE_LAYER:
      return state.map(layer => {
        if (layer.id === action.payload.layerId) {
          layer.visible = !layer.visible;
        }

        return layer;
      });
    default:
      return state;
  }
}

const map = combineReducers({
  command,
  layers
});

export default map;
