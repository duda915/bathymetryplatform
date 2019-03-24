import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Panel } from "primereact/panel";
import { Password } from "primereact/password";
import PropTypes from "prop-types";
import React, { Component } from "react";
import API from "../../../services/API";
import { handleRequest } from "../../utility/requesthandler";

export default class RegisterForm extends Component {
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
      const newUser = {
        username: this.state.username,
        password: this.state.password,
        email: this.state.email
      };

      const api = new API();

      handleRequest({
        requestPromise: api.restUser().registerUser(newUser),
        onSuccess: () => this.props.toggleRegisterForm(false),
        onSuccessMessage: () =>
          "account activation link has been sent to email",
        onErrorMessage: error => error.response.data.message
      });
    } else {
      handleRequest({
        requestPromise: Promise.reject(),
        onErrorMessage: () => "passwords are not the same"
      });
    }
  };

  checkIfPasswordsMatch() {
    return this.state.password === this.state.confirmPassword;
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

            <div className="p-col-12 p-md-4 p-md-offset-8">
              <Button type="submit" label="Register" />
            </div>
          </div>
        </form>
      </Panel>
    );
  }
}

RegisterForm.propTypes = {
  toggleRegisterForm: PropTypes.func.isRequired
};
