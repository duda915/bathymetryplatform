import { FETCH_FEATURE_INFO, MAP_DRAG_BOX } from "./MapActions";
import { combineReducers } from "redux";

function featureInfoURL(state = {}, action) {
  switch (action.type) {
    case FETCH_FEATURE_INFO:
      return action.payload.featureInfoURL;
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
  featureInfoURL,
  dragBoxCoordinates
});

export default map;
