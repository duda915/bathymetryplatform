import React, { Component } from 'react';
import { Panel } from 'primereact/panel';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { FileUpload } from 'primereact/fileupload';
import {Button} from 'primereact/button';
import DataService from '../../services/DataService';

export default class DataManager extends Component {
    constructor(props) {
        super(props);

        this.state = {
            dataName: '',
            date: '',
            dataOwner: '',
            crs: '',
            file: null,
        }

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.onFileSelect = this.onFileSelect.bind(this);
        this.onSelectAbort = this.onSelectAbort.bind(this);

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

    onFileSelect(event) {
        console.log(event.files[0]);
        this.setState({
            file: event.files[0]
        })
    }

    onSelectAbort(event) {
        //prevent default primereact behaviour
        event.xhr.open('GET', '', true);
        this.setState({
            file: null,
        })
    }

    handleSubmit(event) {
        let dateObject = new Date(this.state.date);
        let parseDate = dateObject.getFullYear() + "-" + dateObject.getMonth() + "-" + dateObject.getDate();
        console.log(parseDate);
     
        let urlParams = {
            name: this.state.dataName,
            date: parseDate,
            owner: this.state.dataOwner,
            crs: this.state.crs
        };

        this.dataService.addData(urlParams, this.state.file)
        .then(response => console.log(response));
        event.preventDefault();
    }


    render() {
        return (
            <div className="p-grid p-nogutter p-fluid bathymetry-app-padding">
                <div className="p-col-12 p-lg-2">
                    <Panel header="Add Data">
                        <form onSubmit={this.handleSubmit} autoComplete="off">
                            <div className="p-grid">
                                <div className="p-col-12">
                                    <div className="p-inputgroup">
                                        <span className="p-inputgroup-addon">
                                            <i className="pi pi-user"></i>
                                        </span>
                                        <InputText placeholder="Data name" name="dataName" value={this.state.dataName} onChange={this.handleChange}></InputText>
                                    </div>
                                </div>
                                <div className="p-col-12">
                                    <div className="p-inputgroup">
                                        <span className="p-inputgroup-addon">
                                            <i className="pi pi-user"></i>
                                        </span>
                                        <InputText placeholder="Data owner" name="dataOwner" value={this.state.dataOwner} onChange={this.handleChange}></InputText>
                                    </div>
                                </div>
                                <div className="p-col-12">
                                    <Calendar dateFormat="dd/mm/yy" value={this.state.date} onChange={(e) => this.setState({ date: e.value })} showIcon={true}></Calendar>
                                </div>
                                <div className="p-col-12">
                                    <div className="p-inputgroup">
                                        <span className="p-inputgroup-addon">
                                            <i className="pi pi-user"></i>
                                        </span>
                                        <InputText placeholder="EPSG" name="crs" value={this.state.crs} onChange={this.handleChange}></InputText>
                                    </div>
                                </div>
                                <div className="p-col-12">
                                    <FileUpload mode="basic" accept="*" maxFileSize={10000000} onSelect={this.onFileSelect} onBeforeSend={this.onSelectAbort}/>
                                </div>
                                <div className="p-col-12">
                                    <Button label="Upload" type="submit"/>                            
                                </div>
                            </div>
                        </form>
                    </Panel>
                </div>
            </div>
        )
    }
}