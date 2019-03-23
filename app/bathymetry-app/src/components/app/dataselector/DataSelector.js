import downloadjs from "downloadjs";
import { Button } from "primereact/button";
import { Column } from "primereact/column";
import { ContextMenu } from "primereact/contextmenu";
import { connect } from "react-redux";
import { DataTable } from "primereact/datatable";
import { InputText } from "primereact/inputtext";
import { addLayer } from "../map/MapActions";
import React, { Component } from "react";
import API from "../../../services/API";

export class DataSelectorComponent extends Component {
  constructor(props) {
    super(props);

    this.state = {
      contextMenu: [
        {
          label: "Download",
          icon: "pi pi-download",
          command: event => this.downloadData(this.state.selectedData)
        }
      ]
    };

    this.api = new API();
  }

  componentDidMount() {
    this.fetchDataSets();
  }

  fetchDataSets() {
    this.api
      .restData()
      .getAllDataSets()
      .then(response => this.setState({ data: response.data }))
      .catch(() =>
        this.props.messageService("error", "Error", "cannot fetch datasets")
      );
  }

  showOnMap = () => {
    if (this.state.selection === undefined) {
      this.props.messageService("info", "Info", "Nothing is selected");
      return;
    }

    const layers = this.state.selection.map(value => {
      return {
        id: value.id,
        visible: true
      };
    });

    layers.forEach(layer => this.props.addLayerToMap(layer));
    window.location.hash = "/";
  };

  downloadData(selectedData) {
    this.api
      .restData()
      .downloadDataSet(selectedData.id)
      .then(response =>
        downloadjs(
          response.data,
          `bathymetry${selectedData.id}.csv`,
          "text/plain"
        )
      );
  }

  render() {
    var header = (
      <div style={{ textAlign: "left" }}>
        <div className="p-grid p-nogutter">
          <div className="p-col-8">
            <i className="pi pi-search" style={{ margin: "4px 4px 0 0" }} />
            <span>Select Data</span>
          </div>
          <div className="p-col-4">
            <i className="pi pi-search" style={{ margin: "4px 4px 0 0" }} />
            <InputText
              style={{ width: "45%" }}
              type="search"
              onInput={e => this.setState({ globalFilter: e.target.value })}
              placeholder="Search"
              size="2"
            />
          </div>
        </div>
      </div>
    );

    return (
      <div className="bathymetry-app-padding">
        <div className="p-grid p-fluid ">
          <div className="p-col-12">
            <ContextMenu
              model={this.state.contextMenu}
              ref={el => (this.cm = el)}
              onHide={() => this.setState({ selectedData: null })}
            />
            <DataTable
              header={header}
              globalFilter={this.state.globalFilter}
              value={this.state.data}
              responsive={true}
              scrollable={true}
              scrollHeight="75vh"
              selectionMode="multiple"
              selection={this.state.selection}
              onSelectionChange={e => this.setState({ selection: e.value })}
              metaKeySelection={false}
              contextMenuSelection={this.state.selectedData}
              onContextMenuSelectionChange={e =>
                this.setState({ selectedData: e.value })
              }
              onContextMenu={e => this.cm.show(e.originalEvent)}
            >
              <Column field="id" header="Id" sortable={true} />
              <Column field="name" header="Name" sortable={true} />
              <Column field="measurementDate" header="Date" sortable={true} />
              <Column field="dataOwner" header="Owner" sortable={true} />
            </DataTable>
          </div>
          <div className="p-col-10" />
          <div className="p-col-2">
            <Button label="Show" onClick={this.showOnMap} />
          </div>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    addLayerToMap: layer => dispatch(addLayer(layer))
  };
};

const DataSelector = connect(
  mapStateToProps,
  mapDispatchToProps
)(DataSelectorComponent);

export default DataSelector;
