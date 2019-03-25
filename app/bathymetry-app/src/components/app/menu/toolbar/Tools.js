import { connect } from "react-redux";
import { removeTokens } from "../../../../services/Token";
import { changeLoginState } from "../../../login/LoginActions";
import { Commands } from "../../map/MapCommands";
import { ToolsComponent } from "./ToolsComponent";
import { sendMapCommand, toggleStyle, removeLayers } from "../../map/MapActions";
import {showPanel} from '../MenuPanelActions'

function signOut(dispatch) {
  removeTokens();
  dispatch(changeLoginState(false));
}

function turnOnRegressionService(dispatch) {
  dispatch(showPanel(false));
  dispatch(removeLayers());
  dispatch(
    sendMapCommand({
      commandType: Commands.TURN_ON_REGRESSION_SERVICE_INTERACTION,
      commandPayload: Math.random()
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
    signOut: () => signOut(dispatch),
    changeStyle: () => dispatch(toggleStyle()),
    zoomFit: () =>
      dispatch(
        sendMapCommand({
          commandType: Commands.ZOOM_TO_FIT
        })
      ),
    turnOnRegressionServiceInteraction: () => turnOnRegressionService(dispatch)
  };
};

const Tools = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToolsComponent);
export default Tools;
