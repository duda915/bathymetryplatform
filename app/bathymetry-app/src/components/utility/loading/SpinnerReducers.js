import { TOGGLE_SPINNER } from "./SpinnerActions";
import { combineReducers } from "redux";

function spinnerState(state = false, action) {
  switch (action.type) {
    case TOGGLE_SPINNER:
      return action.payload.spinnerState;
    default:
      return state;
  }
}

const loadingSpinner = combineReducers({
  spinnerState
});

export default loadingSpinner;
