import { FETCH_FEATURE_INFO, MAP_DRAG_BOX } from "./MapActions";
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

const map = combineReducers({
  featureInfoUrl,
  dragBoxCoordinates
});

export default map;
