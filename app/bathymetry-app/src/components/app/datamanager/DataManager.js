import { AddDataForm } from "./adddata/AddDataForm";
import React, { Component } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { ContextMenu } from "primereact/contextmenu";
import API from "../../../services/API";
import { handleRequest } from "../../utility/requesthandler";

export default class DataManager extends Component {
  constructor(props) {
    super(props);

    this.state = {
      contextMenu: [
        {
          label: "Delete",
          icon: "pi pi-fw pi-times",
          command: event => this.deleteDataSet(this.state.selectedData)
        }
      ]
    };
  }

  componentDidMount() {
    this.fetchUserDataSets();
  }

  fetchUserDataSets = () => {
    const api = new API();

    handleRequest({
      requestPromise: api.restData().getUserDataSets(),
      onSuccess: response => this.setState({ data: response.data }),
      onErrorMessage: () => "cannot fetch user datasets"
    });
  };

  deleteDataSet(dataSet) {
    const api = new API();

    handleRequest({
      requestPromise: api.restData().deleteData(dataSet.id),
      onSuccess: () => this.fetchUserDataSets(),
      onSuccessMessage: () => "data deleted",
      onErrorMessage: () => "cannot delete data"
    });
  }

  render() {
    return (
      <div className="bathymetry-app-padding">
        <div className="p-grid p-nogutter" style={{ padding: "10px" }}>
          <div className="p-col-12 p-md-4" />
          <div className="p-col-12 p-md-4">
            <AddDataForm fetchUserDataSets={this.fetchUserDataSets} />
          </div>
          <div className="p-col-12 p-md-4" />

          <div className="p-col-12 p-md-1" />
          <div className="p-col-12 p-md-10" style={{ "paddingTop": "20px" }}>
            My Datasets
            <hr/>
          </div>
          <div className="p-col-12 p-md-1" />

          <div className="p-col-12 p-md-1" />
          <div className="p-col-12 p-md-10">
            <ContextMenu
              model={this.state.contextMenu}
              ref={el => (this.cm = el)}
              onHide={() => this.setState({ selectedData: null })}
            />
            <DataTable
              value={this.state.data}
              paginator={true}
              rows={10}
              rowsPerPageOptions={[5, 10, 20]}
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
        </div>
      </div>
    );
  }
}
