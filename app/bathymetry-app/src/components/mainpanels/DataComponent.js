import React, { Component } from 'react';

import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import { Button } from 'primereact/button';
import DataService from '../../services/DataService';


export default class DataComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
        }
        this.dataService = new DataService();

        this.showOnMap = this.showOnMap.bind(this);
    }

    componentDidMount() {
        this.fetchDataSets();
    }

    fetchDataSets() {
        this.dataService.getDataSets()
        .then(response => this.setState({data: response.data}))
        .then(response => console.log(this.state.data));
    }

    showOnMap() {
        if(this.state.selection === undefined) {
            return;
        }

        console.log(this.state.selection);
        let ids = this.state.selection.map(value => value.id);
        console.log(ids);
        this.props.loadLayersFun(ids);
        window.location.hash = "/";

    }

    render() {
        return (
            <div className="p-grid p-nogutter p-fluid bathymetry-app-padding">
                <div className="p-col-12">
                    <DataTable header="Datasets" value={this.state.data} responsive={true} selectionMode="multiple" selection={this.state.selection}
                    onSelectionChange={e => this.setState({selection: e.value})} metaKeySelection={false}>
                        <Column field="id" header="Id" />
                        <Column field="acquisitionName" header="Name"/>
                        <Column field="acquisitionDate" header="Date"/>
                        <Column field="dataOwner" header="Owner"/>
                    </DataTable>
                </div>
                <div className="p-col p-lg-10"></div>
                <div className="p-col-12 p-lg-2">
                    <Button label="Show" onClick={this.showOnMap}/>
                </div>
            </div>
        );
    }
}