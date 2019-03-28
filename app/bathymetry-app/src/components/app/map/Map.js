import { connect } from "react-redux";
import { MapComponent } from "./MapComponent";
import { sendMapCommand, addLayers } from "./MapActions";
import { Commands } from "./MapCommands";

const mapStateToProps = state => {
  return {
    layers: state.map.layers,
    command: state.map.command,
    style: state.map.style
  };
};

const mapDispatchToProps = dispatch => {
  return {
    sendEmptyCommand: () =>
      dispatch(
        sendMapCommand({
          commandType: Commands.EMPTY
        })
      ),
    addLayer: layer => dispatch(addLayers([layer]))
  };
};

const Map = connect(
  mapStateToProps,
  mapDispatchToProps
)(MapComponent);
export default Map;
