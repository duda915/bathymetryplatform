import React from "react";
import { Password } from "primereact/password";
import { Button } from "primereact/button";
import { Panel } from "primereact/panel";
import API from "../../../services/API";

export class ChangePassword extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      password: "",
      newPassword: "",
      confirmNewPassword: ""
    };

    this.api = new API();
  }

  async verifyNewPassword() {
    return new Promise((resolve, reject) => {
      if (this.state.password === this.state.newPassword) {
        reject("New password is equal to current password");
      } else if (this.state.newPassword !== this.state.confirmNewPassword) {
        reject("confirm password is not equal to new password");
      } else if (this.state.newPassword === "") {
        reject("newpassword cannot be empty");
      }

      this.api
        .restUser()
        .loginUser(this.props.username, this.state.password)
        .then(() => resolve())
        .catch(() => reject("Actual password is not correct"));
    });
  }

  handleSubmit = event => {
    event.preventDefault();

    this.verifyNewPassword()
      .then(() => {
        this.props.loadingService(true);

        const newPassword = {
          newPassword: this.state.newPassword
        };

        this.api
          .restUser()
          .changePassword(newPassword)
          .then(() =>
            this.props.messageService(
              "success",
              "Success",
              "Password changed successfully"
            )
          )
          .catch(() =>
            this.props.messageService(
              "error",
              "Error",
              "Error occured while changing password"
            )
          )
          .finally(() => this.props.loadingService(false));
      })
      .catch(error => this.props.messageService("error", "Error", error))
      .finally(() =>
        this.setState({
          password: "",
          newPassword: "",
          confirmNewPassword: ""
        })
      );
  };

  handleChange = event => {
    this.setState({
      [event.target.name]: event.target.value
    });
  };

  render() {
    return (
      <Panel header="Change Password">
        <div className="p-grid p-nogutter p-justify-center">
          <form onSubmit={this.handleSubmit}>
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-key" />
                </span>
                <Password
                  placeholder="Current password"
                  feedback={false}
                  name="password"
                  value={this.state.password}
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
                  placeholder="New password"
                  feedback={false}
                  name="newPassword"
                  value={this.state.newPassword}
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
                  placeholder="Confirm new password"
                  feedback={false}
                  name="confirmNewPassword"
                  value={this.state.confirmNewPassword}
                  onChange={this.handleChange}
                />
              </div>
            </div>

            <div className="p-col-12">
              <Button
                label="Change password"
                type="submit"
                style={{
                  width: "100%"
                }}
              />
            </div>
          </form>
        </div>
      </Panel>
    );
  }
}
