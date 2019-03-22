import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Panel } from "primereact/panel";
import { Password } from "primereact/password";
import React from "react";
import API from "../../../services/API";

export class LoginForm extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      username: "",
      password: ""
    };
  }

  handleChange = event => {
    this.setState({
      [event.target.name]: event.target.value
    });
  };

  handleSubmit = event => {
    event.preventDefault();
    this.props.loadingService(true);

    const api = new API();
    api
      .restUser()
      .loginUser(this.state.username, this.state.password)
      .then(response => {
        this.props.saveTokens(response);
        this.props.signIn();
      })
      .catch(error =>
        this.props.messageService(
          "error",
          "Error",
          error.response.data.error_description
        )
      )
      .finally(() => this.props.loadingService(false));
  };

  render() {
    return (
      <Panel header="Login">
        <form onSubmit={this.handleSubmit}>
          <div className="p-grid">
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-user" />
                </span>
                <InputText
                  autoComplete="off"
                  placeholder="Username"
                  name="username"
                  value={this.username}
                  onChange={this.handleChange}
                />
              </div>
            </div>
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-key" />
                </span>
                <Password
                  placeholder="Enter password"
                  feedback={false}
                  name="password"
                  value={this.password}
                  onChange={this.handleChange}
                />
              </div>
            </div>
            <div className="p-col-12 p-md-4 p-md-offset-8">
              <Button label="Login" type="submit" />
            </div>
          </div>
        </form>
      </Panel>
    );
  }
}
