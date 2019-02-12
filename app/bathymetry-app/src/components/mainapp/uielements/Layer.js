import React from "react";
import { Checkbox } from "primereact/checkbox";
import { Button } from "primereact/button";
import { transformWithProjections } from "ol/proj";

export class Layer extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      checked: true
    };
  }

  onLayerCheckBox = e => {
    this.setState({
      checked: !this.state.checked
    });
  };

  render() {
    return (
      <div className="p-grid p-justify-between p-align-center">
        <div className="p-col">{this.props.layer.id}</div>
        <div className="p-col">
          <Button className="p-button-success" icon="pi pi-image" onClick={(e) => this.props.zoomToLayer(this.props.layer.id)} />
        </div>
        <div className="p-col">
          <Checkbox
            checked={this.props.layer.visible}
            onChange={(e) => this.props.toggleLayer(this.props.layer.id, !this.props.layer.visible)}
          />
        </div>
      </div>
    );
  }
}
