import React from "react";
import { Checkbox } from "primereact/checkbox";
import { Button } from "primereact/button";

export class Layer extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      checked: true
    };
  }

  onLayerCheckBox = (e) => {
    this.setState({
      checked: !this.state.checked
    })
  }

  render() {
    return (
      <div className="p-grid p-justify-between p-align-center">
        <div className="p-col">{this.props.layerName}</div>
        <div className="p-col">
          <Button className="p-button-success" icon="pi pi-image" />
        </div>
        <div className="p-col">
          <Checkbox checked={this.state.checked} onChange={this.onLayerCheckBox} />
        </div>
      </div>
    );
  }
}
