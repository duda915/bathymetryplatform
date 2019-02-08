import { AddDataForm } from './AddDataForm';
import React, { Component } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ContextMenu } from 'primereact/contextmenu';
import DataService from '../../../services/DataService';
import UserService from '../../../services/UserService';

export default class DataManager extends Component {
    constructor(props) {
        super(props);

        this.state = {
            contextMenu: [
                {
                    label: 'Delete', icon: 'pi pi-fw pi-times',
                    command: (event) => this.deleteDataSet(this.state.selectedData)
                }
            ],

        }

        this.dataService = new DataService();
        this.userService = new UserService();

        this.fetchUserDataSets = this.fetchUserDataSets.bind(this)
    }

    componentDidMount() {
        this.fetchUserDataSets();
    }

    fetchUserDataSets() {
        this.dataService.getUserDataSets()
            .then(response => this.setState({ data: response.data }));
    }

    deleteDataSet(dataSet) {
        this.props.loadingService(true);
        this.dataService.deleteData(dataSet.id)
            .then(response => {
                this.props.messageService('info', 'Success', 'date deleted');
            })
            .catch(response => {
                this.props.messageService('error', 'Error', 'cannot delete data');
            })
            .finally(response => {
                this.props.loadingService(false);
                this.fetchUserDataSets();
            })
    }

    render() {
        
        return (
            <div className="bathymetry-app-padding">
                <div className="p-grid p-fluid ">
                    <div className="p-col-4">
                        <AddDataForm fetchUserDataSets={this.fetchUserDataSets} loadingService={this.props.loadingService} messageService={this.props.messageService}/>
                    </div>
                    <div className="p-col-8 bathymetry-app-padding">
                        <ContextMenu model={this.state.contextMenu} ref={el => this.cm = el} onHide={() => this.setState({ selectedData: null })} />
                        <DataTable header="My Datasets" value={this.state.data} responsive={true} scrollable={true} scrollHeight="75vh" contextMenuSelection={this.state.selectedData}
                            onContextMenuSelectionChange={e => this.setState({ selectedData: e.value })} onContextMenu={e => this.cm.show(e.originalEvent)}>
                            <Column field="id" header="Id" sortable={true} />
                            <Column field="name" header="Name" sortable={true} />
                            <Column field="measurementDate" header="Date" sortable={true} />
                            <Column field="dataOwner" header="Owner" sortable={true} />
                        </DataTable>
                    </div>
                </div>
            </div>
        )
    }
}