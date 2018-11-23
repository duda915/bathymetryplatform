import React, { Component } from 'react';

import {DataTable} from 'primereact/datatable';

export default class DataComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
        }
    }

    // componentDidMount() {
    //     this.fetchDataSets();
    // }

    // fetchDataSets() {
    //     RestFetch.getDataSets(this.parseDataSets);
    // }

    // parseDataSets(json) {
    //     console.log(json);
    //     this.setState({
    //         Sets: json.map((record) => (
    //             <tr key={record.id} onClick={() => this.rowOnclick(record.id)}>
    //                 <td>{record.id}</td>
    //                 <td>{record.acquisitionName}</td>
    //                 <td>{record.acquisitionDate}</td>
    //                 <td>{record.dataOwner}</td>
    //                 <td><Button variant="alert" onClick={() => RestFetch.downloadDataSet(record.id)}>D</Button></td>
    //             </tr>
    //         ))
    //     });
    // }

    render() {
        return (
            <div className="p-grid p-nogutter p-fluid bathymetry-app-padding">
                <div className="p-row-12">
                    <DataTable header="Datasets">
                    
                    </DataTable>
                </div>
            </div>
        );
    }
}