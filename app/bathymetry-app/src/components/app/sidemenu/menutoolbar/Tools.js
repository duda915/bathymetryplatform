import { connect } from "react-redux";
import { removeTokens } from "../../../../services/Token";
import { changeLoginState } from "../../../login/LoginActions";
import { sendMapCommand } from "../../map/MapActions";
import { Commands } from "../../map/MapCommands";
import { ToolsComponent } from "./ToolsComponent";

function signOut(dispatch) {
  removeTokens();
  dispatch(changeLoginState(false));
}

const mapStateToProps = state => {
  return {
    layers: state.map.layers
  };
};

const mapDispatchToProps = dispatch => {
  return {
    signOut: () => signOut(dispatch),
    changeStyle: () =>
      dispatch(
        sendMapCommand({
          commandType: Commands.TOGGLE_STYLE
        })
      ),
    zoomFit: () =>
      dispatch(
        sendMapCommand({
          commandType: Commands.ZOOM_TO_FIT
        })
      )
  };
};

const Tools = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToolsComponent);
export default Tools;
