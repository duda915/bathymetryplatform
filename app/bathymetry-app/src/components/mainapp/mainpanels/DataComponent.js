import React, { Component } from 'react';
import { InputText } from 'primereact/inputtext';

import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { ContextMenu } from 'primereact/contextmenu';
import DataService from '../../../services/DataService';


export default class DataComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
            contextMenu: [
                { label: 'Download', icon: 'pi pi-download', command: (event) => this.downloadData(this.state.selectedData) },
            ]
        }
        this.dataService = new DataService();

        this.showOnMap = this.showOnMap.bind(this);
    }

    componentDidMount() {
        this.fetchDataSets();
    }

    fetchDataSets() {
        this.dataService.getDataSets()
            .then(response => this.setState({ data: response.data }))
            .then(response => console.log(this.state.data));
    }

    showOnMap() {
        if (this.state.selection === undefined) {
            return;
        }

        console.log(this.state.selection);
        let ids = this.state.selection.map(value => {
            return {
                id: value.id,
                visible: true
            }
        });
        console.log(ids);
        this.props.setSelectedLayers(ids);
        window.location.hash = "/";

    }

    downloadData(selectedData) {
        this.dataService.downloadDataSet(selectedData.id)
            .catch(error => console.log(error.response))
    }

    render() {
        var header = <div style={{ 'textAlign': 'left' }}>
            <div className="p-grid p-nogutter">
                <div className="p-col-8">
                    <i className="pi pi-search" style={{ margin: '4px 4px 0 0' }}></i>
                    <span>Select Data</span>
                </div>
                <div className="p-col-4">
                    <i className="pi pi-search" style={{ margin: '4px 4px 0 0' }}></i>
                    <InputText style={{ 'width': '45%' }} type="search" onInput={(e) => this.setState({ globalFilter: e.target.value })} placeholder="Search" size="2" />
                </div>

            </div>
        </div>;

        return (
            <div className="bathymetry-app-padding">
                <div className="p-grid p-fluid ">
                    <div className="p-col-12">
                        <ContextMenu model={this.state.contextMenu} ref={el => this.cm = el} onHide={() => this.setState({ selectedData: null })} />
                        <DataTable header={header} globalFilter={this.state.globalFilter} value={this.state.data} responsive={true} scrollable={true} scrollHeight="75vh" selectionMode="multiple" selection={this.state.selection}
                            onSelectionChange={e => this.setState({ selection: e.value })} metaKeySelection={false}
                            contextMenuSelection={this.state.selectedData} onContextMenuSelectionChange={e => this.setState({ selectedData: e.value })}
                            onContextMenu={e => this.cm.show(e.originalEvent)}>
                            <Column field="id" header="Id" sortable={true} />
                            <Column field="name" header="Name" sortable={true} />
                            <Column field="measurementDate" header="Date" sortable={true} />
                            <Column field="dataOwner" header="Owner" sortable={true} />
                        </DataTable>
                    </div>
                    <div className="p-col-10"></div>
                    <div className="p-col-2">
                        <Button label="Show" onClick={this.showOnMap} />
                    </div>
                </div>
            </div>
        );
    }
}