import React, { Component } from 'react';
import { Panel } from 'primereact/panel';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { FileUpload } from 'primereact/fileupload';
import { Button } from 'primereact/button';
import DataService from '../../../services/DataService';
import UserService from '../../../services/UserService';
import BathymetryDataSetDTO from '../../../services/dtos/BathymetryDataSetDTO';
import EPSGCodeService from '../../../services/EPSGCodeService';
import { AutoComplete } from 'primereact/autocomplete';

export class AddDataForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dataName: '',
            date: '',
            dataOwner: '',
            crs: '',
            file: null,

            epsgSuggestions: null
        }

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.onFileSelect = this.onFileSelect.bind(this);
        this.onSelectAbort = this.onSelectAbort.bind(this);

        this.dataService = new DataService();
        this.userService = new UserService();
        this.epsgService = new EPSGCodeService();


        this.epsgCodes = ['32634', '2222', '222'];
    }

    componentDidMount() {
        this.fetchEpsgCodes();
        this.getUsername();
        this.getTodayDate();
    }

    fetchEpsgCodes() {
        this.epsgService.getEPSGCodes()
            .then(response => {

                this.epsgCodes = response.data.map(code =>
                    code.epsgCode.toString()
                );
                console.log(this.epsgCodes);
            })
    }

    getTodayDate() {
        let date = new Date();

        this.setState({
            date: date
        })
    }

    getUsername() {
        this.userService.getUser()
            .then(response => {
                this.setState({
                    dataOwner: response.data.username,
                });
            })
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
            file: event.files[0],
            dataName: event.files[0].name,
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
        event.preventDefault();

        if(this.state.date == '') {
            this.props.messageService('error', 'Error', 'date must not be empty');
            return;
        }

        const dataSetDTO = new BathymetryDataSetDTO(this.state.crs, this.state.dataName,
            this.state.date, this.state.dataOwner);

        this.props.loadingService(true);

        this.dataService.addData(dataSetDTO, this.state.file)
            .then(response => {
                this.props.messageService('success', "Success", "file upload success");
            })
            .catch(error => {
                this.props.messageService('error', 'Error', error.response.data.message);
            })
            .finally(response => {
                this.props.loadingService(false);
                this.props.fetchUserDataSets();
            });

    }

    itemTemplate(brand) {
        return (
            <div className="p-clearfix">
                <div style={{ fontSize: '16px', float: 'right', margin: '10px 10px 0 0' }}>{brand}</div>
            </div>
        );
    }

    suggestEpsgCode(event) {
        let results = this.epsgCodes.filter((code) => {
            return code.toLowerCase().startsWith(event.query.toLowerCase());
        })

        this.setState({ epsgSuggestions: results });
    }

    render() {
        return <Panel header="Add Data">
            <form onSubmit={this.handleSubmit} autoComplete="off">
                <div className="p-grid">
                    <div className="p-col-12">
                        <FileUpload mode="basic" accept="*" maxFileSize={50000000} onSelect={this.onFileSelect} onBeforeSend={this.onSelectAbort} />
                    </div>
                    <div className="p-col-12">
                        <div className="p-inputgroup">
                            <span className="p-inputgroup-addon">
                                <i className="pi pi-tag"></i>
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
                        <Calendar dateFormat="dd/mm/yy" value={this.state.date} onChange={e => this.setState({
                            date: e.value
                        })} showIcon={true}></Calendar>
                    </div>
                    <div className="p-col-12">
                        <AutoComplete placeholder="EPSG" name="crs" value={this.state.crs} onChange={(e) => this.setState({ crs: e.value })}
                            suggestions={this.state.epsgSuggestions} completeMethod={this.suggestEpsgCode.bind(this)}
                        />
                    </div>

                    <div className="p-col-12">
                        <Button label="Upload" type="submit" />
                    </div>
                </div>
            </form>
        </Panel>;
    }

}
