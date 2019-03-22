import { AddDataForm } from "./adddata/AddDataForm";
import React, { Component } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { ContextMenu } from "primereact/contextmenu";
import API from "../../../services/API";

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

    this.api = new API();
  }

  componentDidMount() {
    this.fetchUserDataSets();
  }

  fetchUserDataSets = () => {
    this.props.loadingService(true);

    this.api
      .restData()
      .getUserDataSets()
      .then(response => this.setState({ data: response.data }))
      .catch(() =>
        this.messageService("error", "Error", "cannot fetch user datasets")
      )
      .finally(() => this.props.loadingService(false));
  };

  deleteDataSet(dataSet) {
    this.props.loadingService(true);

    this.api
      .restData()
      .deleteData(dataSet.id)
      .then(() => this.props.messageService("info", "Delete", "data deleted"))
      .catch(() =>
        this.props.messageService("error", "Error", "cannot delete data")
      )
      .finally(() => {
        this.props.loadingService(false);
        this.fetchUserDataSets();
      });
  }

  render() {
    return (
      <div className="bathymetry-app-padding">
        <div className="p-grid p-fluid ">
          <div className="p-col-4">
            <AddDataForm
              fetchUserDataSets={this.fetchUserDataSets}
              loadingService={this.props.loadingService}
              messageService={this.props.messageService}
            />
          </div>
          <div className="p-col-8 bathymetry-app-padding">
            <ContextMenu
              model={this.state.contextMenu}
              ref={el => (this.cm = el)}
              onHide={() => this.setState({ selectedData: null })}
            />
            <DataTable
              header="My Datasets"
              value={this.state.data}
              responsive={true}
              scrollable={true}
              scrollHeight="75vh"
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
        </div>
      </div>
    );
  }
}
