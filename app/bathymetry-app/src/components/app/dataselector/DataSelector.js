import downloadjs from "downloadjs";
import PropTypes from "prop-types";
import { Button } from "primereact/button";
import { Column } from "primereact/column";
import { ContextMenu } from "primereact/contextmenu";
import { connect } from "react-redux";
import { DataTable } from "primereact/datatable";
import { InputText } from "primereact/inputtext";
import { addLayers, removeLayers } from "../map/MapActions";
import React, { Component } from "react";
import API from "../../../services/API";
import { handleRequest } from "../../utility/requesthandler";
import "./DataSelector.scss";

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
      ],
      selection: []
    };
  }

  componentDidMount() {
    this.fetchDataSets();
  }

  componentWillReceiveProps(props) {
    this.generateVisibleData(props.layers);
  }

  fetchDataSets() {
    const api = new API();

    handleRequest({
      requestPromise: api.restData().getAllDataSets(),
      onSuccess: response => {
        const data = response.data;
        this.setState({ data }, () =>
          this.generateVisibleData(this.props.layers)
        );
      },
      onErrorMessage: () => "cannot fetch datasets"
    });
  }

  goToMap = () => {
    window.location.hash = "/";
  };

  onSelectionChange = event => {
    const layer = event.value;
    layer.visible = true;
    this.props.addLayersToMap([layer]);
  };

  clearSelection = () => {
    this.props.removeLayers();
  };

  generateVisibleData = layers => {
    const layersAdapter = layers.map(layer => layer.id);
    const visibleData = this.state.data.filter(entry => {
      if (layersAdapter.includes(entry.id)) {
        console.log(`includes ${entry.id}`);
      }

      return !layersAdapter.includes(entry.id);
    });
    this.setState({ visibleData });
  };

  downloadData(selectedData) {
    const api = new API();

    handleRequest({
      requestPromise: api.restData().downloadDataSet(selectedData.id),
      onSuccess: response =>
        downloadjs(
          response.data,
          `bathymetry${selectedData.id}.csv`,
          "text/plain"
        )
    });
  }

  render() {
    return (
      <div className="p-grid p-nogutter p-justify-center data-selector-container">
        <div className="p-col-12 p-md-1" />
        <div className="p-col-12 p-md-10" style={{ paddingTop: "5px" }}>
          Select Data
          <hr />
        </div>
        <div className="p-col-12 p-md-1" />

        <div className="p-col-12 p-md-1" />
        <div className="p-col-12 p-md-5" style={{ padding: "5px" }}>
          <InputText
            style={{ width: "100%" }}
            type="search"
            onInput={e => this.setState({ globalFilter: e.target.value })}
            placeholder="Search"
            size="2"
          />
        </div>
        <div className="p-col-12 p-md-6" />

        <div className="p-col-12 p-md-1" />
        <div className="p-col-12 p-md-10 " style={{padding: "5px"}}>
          <ContextMenu
            model={this.state.contextMenu}
            ref={el => (this.cm = el)}
            onHide={() => this.setState({ selectedData: null })}
          />
          <DataTable
            sortField="id"
            sortOrder={-1}
            globalFilter={this.state.globalFilter}
            value={this.state.visibleData}
            paginator={true}
            rows={10}
            rowsPerPageOptions={[5, 10, 20]}
            selectionMode="single"
            onSelectionChange={this.onSelectionChange}
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
        <div className="p-col-12 p-md-1" />

        <div className="p-col-12 p-md-1" />
        <div
          className="p-col-12 p-md-2"
          style={{ paddingBottom: "5px", paddingTop: "5px" }}
        >
          <Button
            label="Go To Map"
            onClick={this.goToMap}
            style={{ width: "100%" }}
          />
        </div>
        <div className="p-col-12 p-md-9" />

        {this.props.layers.length === 0 ? null : (
          <>
            <div className="p-col-12 p-md-1" />
            <div className="p-col-12 p-md-10" style={{ paddingTop: "5px" }}>
              Selected
              <hr />
            </div>
            <div className="p-col-12 p-md-1" />
            <div className="p-col-12 p-md-1" />
            <div className="p-col-12 p-md-2" style={{ padding: "5px" }}>
              <Button
                label="Clear selection"
                onClick={this.clearSelection}
                style={{ width: "100%" }}
              />
            </div>
            <div className="p-col-12 p-md-9" />
            {this.props.layers.map(layer => (
              <React.Fragment key={layer.id}>
                <div className="p-col-12 p-md-1" />
                <div className="p-col-12 p-md-10" style={{ padding: "5px" }}>
                  <div className="selected-data">{layer.name}</div>
                </div>
                <div className="p-md-1" />
              </React.Fragment>
            ))}
          </>
        )}
      </div>
    );
  }
}

DataSelectorComponent.propTypes = {
  layers: PropTypes.array.isRequired,
  removeLayers: PropTypes.func.isRequired,
  addLayersToMap: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {
    layers: state.map.layers
  };
};

const mapDispatchToProps = dispatch => {
  return {
    removeLayers: () => dispatch(removeLayers()),
    addLayersToMap: layers => dispatch(addLayers(layers))
  };
};

const DataSelector = connect(
  mapStateToProps,
  mapDispatchToProps
)(DataSelectorComponent);

export default DataSelector;
