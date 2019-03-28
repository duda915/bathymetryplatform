import PropTypes from "prop-types";
import { connect } from "react-redux";

import Layer from "./Layer";
import { toggleLayer, sendMapCommand } from "../../map/MapActions";
import { Commands } from "../../map/MapCommands";
import "./LayerSwitcher.scss";

import React from "react";

export function LayerSwitcherComponent(props) {
  return (
    <div className="p-col-12 layer-tool">
      <h6
        className="layer-tool-header"
        style={{
          marginTop: "5px",
          marginBottom: "5px",
          fontSize: "13px"
        }}
      >
        Layers
      </h6>
      {props.layers.map(layer => (
        <Layer
          key={layer.id}
          layer={layer}
          zoomToLayer={props.zoomToLayer}
          toggleLayer={props.toggleLayer}
        />
      ))}
    </div>
  );
}

LayerSwitcherComponent.propTypes = {
  layers: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      visible: PropTypes.bool.isRequired
    }).isRequired
  ),
  zoomToLayer: PropTypes.func.isRequired,
  toggleLayer: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {
    layers: state.map.layers
  };
};

function zoomToLayer(dispatch, id) {
  window.location.hash = "/";
  dispatch(
    sendMapCommand({
      commandType: Commands.ZOOM_TO_LAYER,
      commandPayload: id
    })
  );
}

const mapDispatchToProps = dispatch => {
  return {
    zoomToLayer: id => zoomToLayer(dispatch, id),
    toggleLayer: id => dispatch(toggleLayer(id))
  };
};

const LayerSwitcher = connect(
  mapStateToProps,
  mapDispatchToProps
)(LayerSwitcherComponent);

export default LayerSwitcher;
