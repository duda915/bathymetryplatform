import React, { Component } from 'react';
import {RestFetch} from '../utility/Rest';
import Form from 'react-bootstrap/lib/Form';
import Button from 'react-bootstrap/lib/Button';
import DataService from '../../services/DataService';

class AddData extends Component {
    constructor(props) {
        super(props);

        this.state = {
            name: '',
            date: '',
            owner: '',
            crs: '',
            file: null,
        }

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleFileChange = this.handleFileChange.bind(this);
        this.dataService = new DataService();
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleFileChange(event) {
        this.setState({
            file: event.target.files[0]
        })
    }

    handleSubmit(event) {
        let urlParams = {
            name: this.state.name,
            date: this.state.date,
            owner: this.state.owner,
            crs: this.state.crs
        };
        RestFetch.addDataRequest(urlParams, this.state.file);
        event.preventDefault();
    }

    render() {
        return(
            // <Form onSubmit={this.handleSubmit}>
            //     <Form.Group controlId="formName">
            //         <Form.Label>Data name</Form.Label>
            //         <Form.Control required type="text" autoComplete="off" placeholder="Name" name="name" value={this.state.name} onChange={this.handleChange} />
            //     </Form.Group>

            //     <Form.Group controlId="formOwner">
            //         <Form.Label>Data owner</Form.Label>
            //         <Form.Control required type="text" autoComplete="off" placeholder="Owner" name="owner" value={this.state.owner} onChange={this.handleChange} />
            //     </Form.Group>

            //     <Form.Group controlId="formDate">
            //         <Form.Label>Date</Form.Label>
            //         <Form.Control required type="date" placeholder="Date" name="date" value={this.state.date} onChange={this.handleChange} />
            //     </Form.Group>

            //     <Form.Group controlId="formCRS">
            //         <Form.Label>CRS</Form.Label>
            //         <Form.Control required type="number" autoComplete="off" placeholder="CRS" name="crs" value={this.state.crs} onChange={this.handleChange} />
            //     </Form.Group>

            //     <Form.Group controlId="formFile">
            //         <Form.Label>File</Form.Label>
            //         <Form.Control required type="file" name="file" onChange={this.handleFileChange} />
            //     </Form.Group>

            //     <Button variant="primary" type="submit" >
            //         Submit
            //     </Button>
            // </Form>
            <div className="p-grid">
                <form>
                    <div className="p-row-12">
                    
                    </div>
                </form>
            </div>
        )
    }
}

export default AddData;