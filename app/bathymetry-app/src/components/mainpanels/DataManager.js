import React, { Component } from 'react';
import { Panel } from 'primereact/panel';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { FileUpload } from 'primereact/fileupload';
import { Button } from 'primereact/button';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import {Growl} from 'primereact/growl';
import {ContextMenu} from 'primereact/contextmenu';
import DataService from '../../services/DataService';
import LoadingComponent from '../utility/LoadingComponent';

export default class DataManager extends Component {
    constructor(props) {
        super(props);

        this.state = {
            dataName: '',
            date: '',
            dataOwner: '',
            crs: '',
            file: null,
            contextMenu: [
                {label: 'Delete', icon: 'pi pi-fw pi-times', command: (event) => this.deleteDataSet(this.state.selectedData)}
            ]
        }


        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.onFileSelect = this.onFileSelect.bind(this);
        this.onSelectAbort = this.onSelectAbort.bind(this);

        this.dataService = new DataService();
    }

    componentDidMount() {
        this.fetchUserDataSets();
    }

    fetchUserDataSets() {
        this.dataService.getUserDataSets()
            .then(response => this.setState({ data: response.data }));
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

        this.progress.showProgress(true);

        this.dataService.addData(urlParams, this.state.file)
            .then(response => {
                this.growl.show({severity: 'success', summary: 'Success', detail: 'File upload success', closable: false})
            })
            .catch(error => {
                this.growl.show({severity: 'error', summary: 'Error', detail: 'File upload failed', closable: false})
            })
            .finally(response => {
                this.progress.showProgress(false);
                this.fetchUserDataSets();
            });

        event.preventDefault();
    }

    deleteDataSet(dataSet) {
        this.progress.showProgress(true);
        this.dataService.deleteUserData(dataSet.id)  
        .then(response => this.growl.show({severity: 'info', summary: 'Success', detail: 'Data deleted.', closable: false}))
        .catch(response => this.growl.show({severity: 'error', summary: 'Error', detail: 'Cannot delete data', closable: false}))
        .finally(response => {
            this.progress.showProgress(false);
            this.fetchUserDataSets();
        })
    }


    render() {
        return (
            <div className="bathymetry-app-padding">
                <Growl ref={(ref) => this.growl = ref} />
                <LoadingComponent ref={(ref) => this.progress = ref}/>
                <div className="p-grid p-fluid ">
                    <div className="p-col-12 p-lg-3">
                        <Panel header="Add Data">
                            <form onSubmit={this.handleSubmit} autoComplete="off">
                                <div className="p-grid">
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
                                        <Calendar dateFormat="dd/mm/yy" value={this.state.date} onChange={(e) => this.setState({ date: e.value })} showIcon={true}></Calendar>
                                    </div>
                                    <div className="p-col-12">
                                        <div className="p-inputgroup">
                                            <span className="p-inputgroup-addon">
                                                <i className="pi pi-globe"></i>
                                            </span>
                                            <InputText placeholder="EPSG" name="crs" value={this.state.crs} onChange={this.handleChange}></InputText>
                                        </div>
                                    </div>
                                    <div className="p-col-12">
                                        <FileUpload mode="basic" accept="*" maxFileSize={10000000} onSelect={this.onFileSelect} onBeforeSend={this.onSelectAbort} />
                                    </div>
                                    <div className="p-col-12">
                                        <Button label="Upload" type="submit" />
                                    </div>
                                </div>
                            </form>
                        </Panel>
                    </div>
                    <div className="p-col-12 p-lg-9 bathymetry-app-padding">
                        <ContextMenu model={this.state.contextMenu} ref={el => this.cm = el} onHide={() => this.setState({selectedData: null})}/>
                        <DataTable header="My Datasets" value={this.state.data} responsive={true} contextMenuSelection={this.state.selectedData}
                        onContextMenuSelectionChange={e => this.setState({selectedData: e.value})} onContextMenu={e => this.cm.show(e.originalEvent)}>
                            <Column field="id" header="Id" />
                            <Column field="acquisitionName" header="Name" />
                            <Column field="acquisitionDate" header="Date" />
                            <Column field="dataOwner" header="Owner" />
                        </DataTable>
                    </div>
                </div>
            </div>
        )
    }
}