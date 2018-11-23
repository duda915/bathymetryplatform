import React, { Component } from 'react';

import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import DataService from '../../services/DataService';

export default class DataComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
        }
        this.dataService = new DataService();
    }

    componentDidMount() {
        this.fetchDataSets();
    }

    fetchDataSets() {
        this.dataService.getDataSets()
        .then(response => this.setState({data: response.data}))
        .then(response => console.log(this.state.data));
    }

    render() {
        return (
            <div className="p-grid p-nogutter p-fluid bathymetry-app-padding">
                <div className="p-row-12">
                    <DataTable header="Datasets" value={this.state.data} responsive={true} selectionMode="multiple" selection={this.state.selection}
                    onSelectionChange={e => this.setState({selection: e.value})} metaKeySelection={false}>
                        <Column field="id" header="Id" />
                        <Column field="acquisitionName" header="Name"/>
                        <Column field="acquisitionDate" header="Date"/>
                        <Column field="dataOwner" header="Owner"/>
                    </DataTable>
                </div>
            </div>
        );
    }
}