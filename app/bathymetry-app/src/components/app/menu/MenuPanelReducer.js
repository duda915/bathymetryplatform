import { TOGGLE_PANEL } from "./MenuPanelActions";
import { combineReducers } from "redux";

function togglePanel(state = false, action) {
  switch (action.type) {
    case TOGGLE_PANEL:
      return !state;
    default:
      return state;
  }
}

const menuPanel = combineReducers({
  togglePanel
});

export default menuPanel;
