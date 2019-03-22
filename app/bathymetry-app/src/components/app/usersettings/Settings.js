import { Panel } from "primereact/panel";
import React from "react";
import API from "../../../services/API";
import { ChangePassword } from "./changepassword/ChangePassword";

export default class Settings extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      user: {}
    };

    this.api = new API();
  }

  componentDidMount() {
    this.fetchUserData();
  }

  fetchUserData() {
    this.props.loadingService(true);

    this.api
      .restUser()
      .getUser()
      .then(response =>
        this.setState({
          user: {
            username: response.data.username,
            email: response.data.email,
            authorities: response.data.userAuthorities.map(authority => (
              <li key={authority.authority.id}>
                {authority.authority.authorityName}
              </li>
            ))
          }
        })
      )
      .catch(() =>
        this.props.messageService("error", "Error", "cannot load user data")
      );

    this.api
      .restData()
      .getUserDataSets()
      .then(response => this.setState({ datasets: response.data.length }))
      .catch(() =>
        this.props.messageService("error", "Error", "cannot load datasets")
      )
      .finally(() => this.props.loadingService(false));
  }

  render() {
    return (
      <div className="bathymetry-app-padding">
        <Panel header="Account">
          <div className="p-grid">
            <div className="p-col-4">
              <Panel header="Info">
                Username: {this.state.user.username} <br />
                Email: {this.state.user.email} <br />
                Uploaded datasets: {this.state.datasets} <br />
                Authorities:
                <ul>{this.state.user.authorities}</ul>
              </Panel>
            </div>

            <div className="p-col-4">
              <ChangePassword
                username={this.state.user.username}
                messageService={this.props.messageService}
                loadingService={this.props.loadingService}
              />
            </div>
          </div>
        </Panel>
      </div>
    );
  }
}
