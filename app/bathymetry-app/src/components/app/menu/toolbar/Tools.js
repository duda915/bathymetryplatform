import { connect } from "react-redux";
import {
  removeLayers,
  sendMapCommand,
  toggleStyle
} from "../../map/MapActions";
import { Commands } from "../../map/MapCommands";
import { showPanel } from "../MenuPanelActions";
import { ToolsComponent } from "./ToolsComponent";

function sendToMap() {
  window.location.hash = "/";
}

function turnOnRegressionService(dispatch) {
  sendToMap();
  dispatch(showPanel(false));
  dispatch(removeLayers());
  dispatch(
    sendMapCommand({
      commandType: Commands.TURN_ON_REGRESSION_SERVICE_INTERACTION,
      commandPayload: Math.random()
    })
  );
}

function toggleStyleFun(dispatch) {
  sendToMap();
  dispatch(showPanel(false));
  dispatch(toggleStyle());
}

function zoomFit(dispatch) {
  sendToMap();
  dispatch(showPanel(false));
  dispatch(
    sendMapCommand({
      commandType: Commands.ZOOM_TO_FIT
    })
  );
}

const mapStateToProps = state => {
  return {
    layers: state.map.layers
  };
};

const mapDispatchToProps = dispatch => {
  return {
    changeStyle: () => toggleStyleFun(dispatch),
    zoomFit: () => zoomFit(dispatch),
    turnOnRegressionServiceInteraction: () => turnOnRegressionService(dispatch)
  };
};

const Tools = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToolsComponent);
export default Tools;
