import { AutoComplete } from "primereact/autocomplete";
import { Button } from "primereact/button";
import { Calendar } from "primereact/calendar";
import { FileUpload } from "primereact/fileupload";
import { InputText } from "primereact/inputtext";
import { Panel } from "primereact/panel";
import React from "react";
import API from "../../../services/API";

export class AddDataForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataName: "",
      date: "",
      dataOwner: "",
      crs: "",
      file: null,

      epsgSuggestions: null
    };

    this.api = new API();

    this.epsgCodes = [];
  }

  componentDidMount() {
    this.fetchEpsgCodes();
    this.getUsername();
    this.getTodaysDate();
  }

  fetchEpsgCodes() {
    this.api
      .restData()
      .getEPSGCodes()
      .then(
        response =>
          (this.epsgCodes = response.data.map(code => code.epsgCode.toString()))
      )
      .catch(() =>
        this.props.messageService("error", "Error", "cannot fetch epsg codes")
      );
  }

  getTodaysDate() {
    this.setState({
      date: new Date()
    });
  }

  getUsername() {
    this.api
      .restUser()
      .getUser()
      .then(response => this.setState({ dataOwner: response.data.username }))
      .catch(() =>
        this.props.messageService("error", "Error", "cannot fetch username")
      );
  }

  handleChange = event => {
    this.setState({
      [event.target.name]: event.target.value
    });
  };

  onFileSelect = event => {
    this.setState({
      file: event.files[0],
      dataName: event.files[0].name
    });
  };

  onSelectAbort = event => {
    //Prevent default PrimeReact control behaviour
    event.xhr.open("GET", "", true);
    this.setState({
      file: null
    });
  };

  handleSubmit = event => {
    event.preventDefault();

    const newDataSet = {
      name: this.state.dataName,
      dataOwner: this.state.dataOwner,
      measurementDate: this.state.date,
      epsgCode: this.state.crs
    };

    this.props.loadingService(true);

    this.api
      .restData()
      .uploadData(newDataSet, this.state.file)
      .then(() =>
        this.props.messageService("success", "Success", "file upload success")
      )
      .catch(error =>
        this.props.messageService("error", "Error", error.response.data.message)
      )
      .finally(() => {
        this.props.loadingService(false);
        this.props.fetchUserDataSets();
      });
  };

  suggestEpsgCode(event) {
    const results = this.epsgCodes.filter(code => {
      return code.toLowerCase().startsWith(event.query.toLowerCase());
    });

    this.setState({ epsgSuggestions: results });
  }

  render() {
    return (
      <Panel header="Add Data">
        <form onSubmit={this.handleSubmit} autoComplete="off">
          <div className="p-grid">
            <div className="p-col-12">
              <FileUpload
                mode="basic"
                accept="*"
                maxFileSize={50000000}
                onSelect={this.onFileSelect}
                onBeforeSend={this.onSelectAbort}
              />
            </div>
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-tag" />
                </span>
                <InputText
                  placeholder="Data name"
                  name="dataName"
                  value={this.state.dataName}
                  onChange={this.handleChange}
                />
              </div>
            </div>
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-user" />
                </span>
                <InputText
                  placeholder="Data owner"
                  name="dataOwner"
                  value={this.state.dataOwner}
                  onChange={this.handleChange}
                />
              </div>
            </div>
            <div className="p-col-12">
              <Calendar
                dateFormat="dd/mm/yy"
                value={this.state.date}
                onChange={e =>
                  this.setState({
                    date: e.value
                  })
                }
                showIcon={true}
              />
            </div>
            <div className="p-col-12">
              <AutoComplete
                placeholder="EPSG"
                name="crs"
                value={this.state.crs}
                onChange={e => this.setState({ crs: e.value })}
                suggestions={this.state.epsgSuggestions}
                completeMethod={this.suggestEpsgCode.bind(this)}
              />
            </div>

            <div className="p-col-12">
              <Button label="Upload" type="submit" />
            </div>
          </div>
        </form>
      </Panel>
    );
  }
}
