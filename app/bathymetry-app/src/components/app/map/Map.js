import { connect } from "react-redux";
import { MapComponent } from "./MapComponent";
import {sendMapCommand} from './MapActions'
import {Commands} from './MapCommands'

const mapStateToProps = state => {
  return {
    layers: state.map.layers,
    command: state.map.command,
    style: state.map.style
  };
};

const mapDispatchToProps = dispatch => {
  return {
    sendEmptyCommand: () => dispatch(sendMapCommand({
      commandType: Commands.EMPTY
    }))
  };
};

const Map = connect(
  mapStateToProps,
  mapDispatchToProps
)(MapComponent);
export default Map;
