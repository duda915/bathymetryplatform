import { connect } from "react-redux";
import { MapComponent } from "./MapComponent";

const mapStateToProps = state => {
  return {
    layers: state.map.layers,
    command: state.map.command,
    style: state.map.style
  };
};

const mapDispatchToProps = dispatch => {
  return {};
};

const Map = connect(
  mapStateToProps,
  mapDispatchToProps
)(MapComponent);
export default Map;
