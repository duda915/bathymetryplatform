import { Panel } from "primereact/panel";
import React from "react";
import API from "../../../services/API";
import { ChangePassword } from "./changepassword/ChangePassword";
import { handleRequest } from "../../utility/requesthandler";

export default class Settings extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      user: {}
    };
  }

  componentDidMount() {
    this.fetchUserData();
  }

  fetchUserData() {
    const api = new API();

    handleRequest({
      requestPromise: api.restUser().getUser(),
      onSuccess: response => {
        const { username, email, userAuthorities } = response.data;
        this.setState({
          user: {
            username: username,
            email: email,
            authorities: userAuthorities.map(authority => (
              <li key={authority.authority.id}>
                {authority.authority.authorityName}
              </li>
            ))
          }
        });
      },
      onErrorMessage: () => "cannot load user data"
    });

    handleRequest({
      requestPromise: api.restData().getUserDataSets(),
      onSuccess: response => this.setState({ datasets: response.data.length }),
      onErrorMessage: () => "cannot load datasets"
    });
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
              />
            </div>
          </div>
        </Panel>
      </div>
    );
  }
}
