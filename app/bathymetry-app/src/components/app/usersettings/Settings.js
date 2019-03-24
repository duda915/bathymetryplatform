import React from "react";
import API from "../../../services/API";
import { handleRequest } from "../../utility/requesthandler";
import { ChangePassword } from "./changepassword/ChangePassword";

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
      <div className="p-grid p-nogutter" style={{ padding: "10px" }}>
        <div className="p-col-12 p-md-4" />
        <div className="p-col-12 p-md-4" >
          Account Info
          <hr />
        </div>
        <div className="p-col-12 p-md-4" />

        <div className="p-col-12 p-md-4" />
        <div className="p-col-12 p-md-4" style={{ padding: "10px" }}>
          Username: {this.state.user.username} <br />
          Email: {this.state.user.email} <br />
          Uploaded datasets: {this.state.datasets} <br />
          Authorities:
          <ul>{this.state.user.authorities}</ul>
        </div>
        <div className="p-col-12 p-md-4" />

        <div className="p-col-12 p-md-4" />
        <div className="p-col-12 p-md-4">
          <ChangePassword username={this.state.user.username} />
        </div>
        <div className="p-col-12 p-md-4" />
      </div>
    );
  }
}
