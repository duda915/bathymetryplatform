import { connect } from "react-redux";
import { MapComponent } from "./MapComponent";
import { sendMapCommand } from "./MapActions";

const mapStateToProps = state => {
  return {
    layers: state.map.layers,
    command: state.map.command
  };
};

const mapDispatchToProps = dispatch => {
  return {
    emptyCommand: () =>
      sendMapCommand({
        commandType: null,
        commandPayload: null
      })
  };
};

const Map = connect(
  mapStateToProps,
  mapDispatchToProps
)(MapComponent);
export default Map;
