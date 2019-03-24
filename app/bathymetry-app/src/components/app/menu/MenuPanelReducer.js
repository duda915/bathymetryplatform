import { SHOW_PANEL } from "./MenuPanelActions";
import { combineReducers } from "redux";

function showPanel(state = false, action) {
  switch (action.type) {
    case SHOW_PANEL:
      return action.payload.show;
    default:
      return state;
  }
}

const menuPanel = combineReducers({
  showPanel
});

export default menuPanel;
