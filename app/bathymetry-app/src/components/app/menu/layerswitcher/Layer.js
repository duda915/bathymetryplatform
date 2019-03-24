import { Button } from "primereact/button";
import { Checkbox } from "primereact/checkbox";
import React from "react";
import PropTypes from "prop-types";

export default class Layer extends React.Component {
  render() {
    return (
      <div className="p-grid p-nogutter p-align-center  layer-tool__layer">
        <div className="p-col-8 layer-tool__layer__name">
          {this.props.layer.name}
        </div>
        <div className="p-col-2">
          <Button
            className="p-button tools-button flat-button"
            icon="pi pi-search"
            onClick={e => this.props.zoomToLayer(this.props.layer.id)}
          />
        </div>
        <div className="p-col-2">
          <Checkbox
            checked={this.props.layer.visible}
            onChange={() => this.props.toggleLayer(this.props.layer.id)}
          />
        </div>
      </div>
    );
  }
}

Layer.propTypes = {
  layer: PropTypes.shape({
    id: PropTypes.number.isRequired,
    visible: PropTypes.bool.isRequired
  }),
  zoomToLayer: PropTypes.func.isRequired,
  toggleLayer: PropTypes.func.isRequired
};
