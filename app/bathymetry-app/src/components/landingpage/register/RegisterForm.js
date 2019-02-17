import React, { Component } from "react";
import { Panel } from "primereact/panel";
import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";
import { Button } from "primereact/button";
import API from "../../../services/API";

export class RegisterForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      password: "",
      confirmPassword: "",
      email: ""
    };
  }

  handleChange = event => {
    this.setState({
      [event.target.name]: event.target.value
    });
  };

  onSubmit = event => {
    event.preventDefault();
    if (this.checkIfPasswordsMatch()) {
      this.props.loadingService(true);

      const newUser = {
        username: this.state.username,
        password: this.state.password,
        email: this.state.email
      };

      const api = new API();
      api
        .restUser()
        .registerUser(newUser)
        .then(() => {
          this.props.messageService(
            "success",
            "Success",
            "account activation link has been sent to email"
          );
          this.props.toggleRegisterForm(false);
        })
        .catch(error =>
          this.props.messageService(
            "error",
            "Error",
            error.response.data.message
          )
        )
        .finally(() => this.props.loadingService(false));
    } else {
      this.props.messageService("warn", "Error", "passwords are not the same");
    }
  };

  checkIfPasswordsMatch() {
    if (this.state.password !== this.state.confirmPassword) {
      return false;
    }
    return true;
  }

  render() {
    return (
      <Panel header="Register">
        <form onSubmit={this.onSubmit}>
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
                  <i className="pi pi-home" />
                </span>
                <InputText
                  autoComplete="off"
                  placeholder="Email"
                  name="email"
                  value={this.email}
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
                  feedback={false}
                  autoComplete="off"
                  placeholder="Enter password"
                  name="password"
                  value={this.password}
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
                  feedback={false}
                  autoComplete="off"
                  placeholder="Confirm password"
                  name="confirmPassword"
                  value={this.confirmPassword}
                  onChange={this.handleChange}
                />
              </div>
            </div>

            <div className="p-col-4 p-offset-8">
              <Button type="submit" label="Register" />
            </div>
          </div>
        </form>
      </Panel>
    );
  }
}
