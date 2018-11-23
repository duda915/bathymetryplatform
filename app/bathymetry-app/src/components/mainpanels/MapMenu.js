import React, { Component } from 'react';
// import {RestFetch} from '../utility/Rest';
import DataService from '../../services/DataService';
import Tabs from 'react-bootstrap/lib/Tabs';
import Tab from 'react-bootstrap/lib/Tab';
import Table from 'react-bootstrap/lib/Table';
import Button from 'react-bootstrap/lib/Button';
import AddData from './AddData';

class MapMenu extends Component {
    constructor(props) {
        super(props);

        this.state = {
            Sets: null
        }
        
        this.parseDataSets = this.parseDataSets.bind(this);
        this.rowOnclick = this.rowOnclick.bind(this);
        this.dataService = new DataService();
    }

    componentDidMount() {
        this.fetchDataSets();
    }

    fetchDataSets() {
        this.dataService.getDataSets().then(response => this.parseDataSets(response.data));
    }

    parseDataSets(json) {
        console.log(json);
        this.setState({
            Sets: json.map((record) => (
                <tr key={record.id} onClick={() => this.rowOnclick(record.id)}>
                    <td>{record.id}</td>
                    <td>{record.acquisitionName}</td>
                    <td>{record.acquisitionDate}</td>
                    <td>{record.dataOwner}</td>
                </tr>
            ))
        });
    }

    rowOnclick(key) {
        this.props.loadLayer(key);
    }

    render() {
        return(
            <div className='h-100' style={{overflow: 'auto'}}>
                <Tabs defaultActiveKey="data" >
                    <Tab eventKey="data" title="Data Sets">
                            <Table striped bordered hover size="sm" variant="dark">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Name</th>
                                        <th>Date</th>
                                        <th>Owner</th>
                                        <th>Download</th>
                                    </tr>
                                </thead>
                                <tbody >
                                    {this.state.Sets}
                                </tbody>
                            </Table>
                    </Tab>
                    <Tab eventKey="add" title="Add Data">
                        <AddData></AddData>
                    </Tab>
                </Tabs>
            </div>
        );
    }
}

export default MapMenu;