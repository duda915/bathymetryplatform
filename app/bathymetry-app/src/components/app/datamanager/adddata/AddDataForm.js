import { AutoComplete } from "primereact/autocomplete";
import PropTypes from "prop-types";
import { Button } from "primereact/button";
import { Calendar } from "primereact/calendar";
import { FileUpload } from "primereact/fileupload";
import { InputText } from "primereact/inputtext";
import { Panel } from "primereact/panel";
import React from "react";
import API from "../../../../services/API";
import { handleRequest } from "../../../utility/requesthandler";

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

    this.epsgCodes = [];
  }

  componentDidMount() {
    this.fetchEpsgCodes();
    this.getUsername();
    this.getTodaysDate();
  }

  fetchEpsgCodes() {
    const api = new API();

    handleRequest({
      requestPromise: api.restData().getEPSGCodes(),
      onSuccess: response =>
        (this.epsgCodes = response.data.map(code => code.epsgCode.toString())),
      onErrorMessage: () => "cannot fetch epsg codes"
    });
  }

  getTodaysDate() {
    this.setState({
      date: new Date()
    });
  }

  getUsername() {
    const api = new API();

    handleRequest({
      requestPromise: api.restUser().getUser(),
      onSuccess: response =>
        this.setState({ dataOwner: response.data.username }),
      onErrorMessage: () => "cannot fetch username"
    });
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

    const api = new API();

    const newDataSet = {
      name: this.state.dataName,
      dataOwner: this.state.dataOwner,
      measurementDate: this.state.date,
      epsgCode: this.state.crs
    };

    handleRequest({
      requestPromise: api.restData().uploadData(newDataSet, this.state.file),
      onSuccess: () => this.props.fetchUserDataSets(),
      onSuccessMessage: () => "file upload success",
      onErrorMessage: error => error.response.data.message
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
      <form onSubmit={this.handleSubmit} autoComplete="off">
        <div className="p-grid p-fluid p-nogutter">
          <div className="p-col-12" >
            Add Data
            <hr />
          </div>

          <div className="p-col-12" style={{ padding: "5px" }}>
            <FileUpload
              mode="basic"
              accept="*"
              maxFileSize={50000000}
              onSelect={this.onFileSelect}
              onBeforeSend={this.onSelectAbort}
            />
          </div>
          <div className="p-col-12" style={{ padding: "5px" }}>
            <div className="p-inputgroup">
              <span className="p-inputgroup-addon">
                <i className="pi pi-tag" />
              </span>
              <InputText
              style={{width: "100%"}}
                placeholder="Data name"
                name="dataName"
                value={this.state.dataName}
                onChange={this.handleChange}
              />
            </div>
          </div>
          <div className="p-col-12" style={{ padding: "5px" }}>
            <div className="p-inputgroup">
              <span className="p-inputgroup-addon">
                <i className="pi pi-user" />
              </span>
              <InputText
              style={{width: "100%"}}

                placeholder="Data owner"
                name="dataOwner"
                value={this.state.dataOwner}
                onChange={this.handleChange}
              />
            </div>
          </div>
          <div className="p-col-12" style={{ padding: "5px" }}>
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
          <div className="p-col-12" style={{ padding: "5px" }}>
            <AutoComplete
              placeholder="EPSG"
              name="crs"
              value={this.state.crs}
              onChange={e => this.setState({ crs: e.value })}
              suggestions={this.state.epsgSuggestions}
              completeMethod={this.suggestEpsgCode.bind(this)}
            />
          </div>

          <div className="p-col-12" style={{ padding: "5px" }}>
            <Button label="Upload" type="submit" />
          </div>
        </div>
      </form>
    );
  }
}

AddDataForm.propTypes = {
  fetchUserDataSets: PropTypes.func.isRequired
};
