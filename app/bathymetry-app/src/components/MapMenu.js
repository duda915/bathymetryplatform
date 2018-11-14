import React, { Component } from 'react';
import {RestFetch} from './utility/Rest';
import Tabs from 'react-bootstrap/lib/Tabs';
import Tab from 'react-bootstrap/lib/Tab';
import Table from 'react-bootstrap/lib/Table';

class MapMenu extends Component {
    constructor(props) {
        super(props);

        this.state = {
            Sets: null
        }
        
        this.parseDataSets = this.parseDataSets.bind(this);
    }

    componentDidMount() {
        this.fetchDataSets();
    }

    fetchDataSets() {
        RestFetch.getDataSets(this.parseDataSets);
    }

    parseDataSets(json) {
        console.log(json);
        this.setState({
            Sets: json.map((record) => (
                <tr key={record.id}>
                    <td>{record.id}</td>
                    <td>{record.acquisitionName}</td>
                    <td>{record.acquisitionDate}</td>
                    <td>{record.dataOwner}</td>
                </tr>
            ))
        });
    }

    render() {
        return(
            <div className='h-100' style={{overflow: 'auto'}}>
                <Tabs defaultActiveKey="data" >
                    <Tab eventKey="data" title="Data Sets"  >
                            <Table striped bordered hover>
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Name</th>
                                        <th>Date</th>
                                        <th>Owner</th>
                                    </tr>
                                </thead>
                                <tbody >
                                    {this.state.Sets}
                                </tbody>
                            </Table>
                    </Tab>
                    <Tab eventKey="add" title="Add Data">
                    
                    </Tab>
                </Tabs>
            </div>
        );
    }
}

export default MapMenu;