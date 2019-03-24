import { Button } from "primereact/button";
import { Password } from "primereact/password";
import React from "react";
import API from "../../../../services/API";
import { handleRequest } from "../../../utility/requesthandler";

export class ChangePassword extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      password: "",
      newPassword: "",
      confirmNewPassword: ""
    };
  }

  async verifyNewPassword() {
    const api = new API();

    return new Promise((resolve, reject) => {
      if (this.state.password === this.state.newPassword) {
        reject("New password is equal to current password");
      } else if (this.state.newPassword !== this.state.confirmNewPassword) {
        reject("confirm password is not equal to new password");
      } else if (this.state.newPassword === "") {
        reject("newpassword cannot be empty");
      }

      handleRequest({
        requestPromise: api
          .restUser()
          .loginUser(this.props.username, this.state.password),
        onSuccess: () => resolve(),
        onError: () => reject("actual password is not correct")
      });
    });
  }

  handleSubmit = event => {
    event.preventDefault();

    const api = new API();

    handleRequest({
      requestPromise: this.verifyNewPassword(),
      onSuccess: () => {
        const newPassword = {
          newPassword: this.state.newPassword
        };

        this.setState({
          password: "",
          newPassword: "",
          confirmNewPassword: ""
        });

        handleRequest({
          requestPromise: api.restUser().changePassword(newPassword),
          onSuccessMessage: () => "password changed successfully",
          onErrorMessage: () => "error occured while changing password",
          onError: error => console.log(error.response)
        });
      },
      onErrorMessage: error => error
    });
  };

  handleChange = event => {
    this.setState({
      [event.target.name]: event.target.value
    });
  };

  render() {
    return (
      <form onSubmit={this.handleSubmit}>
        <div className="p-grid p-nogutter p-fluid">
        <div className="p-col-12">
          Change Password
          <hr/>
        </div>
          <div className="p-col-12" style={{padding: "10px"}}>
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

          <div className="p-col-12" style={{padding: "10px"}}>
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

          <div className="p-col-12" style={{padding: "10px"}}>
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

          <div className="p-col-12" style={{padding: "10px"}}>
            <Button
              label="Change password"
              type="submit"
              style={{
                width: "100%"
              }}
            />
          </div>
        </div>
      </form>
    );
  }
}
