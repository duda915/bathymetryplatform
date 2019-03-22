import React from "react";
import PropTypes from "prop-types";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Panel } from "primereact/panel";
import { Password } from "primereact/password";
import { connect } from "react-redux";
import { toggleSpinner } from "../../utility/loading/SpinnerActions";
import { showMessage } from "../../utility/messaging/MessageActions";
import { changeLoginState } from "../LoginActions";
import { saveTokens } from "../../../services/Token";

import API from "../../../services/API";

class LoginFormComponent extends React.Component {
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
    this.props.spinner(true);

    const api = new API();
    api
      .restUser()
      .loginUser(this.state.username, this.state.password)
      .then(response => {
        saveTokens(response);
        this.props.signIn();
      })
      .catch(error =>
        this.props.message(
          "error",
          "Error",
          error.response.data.error_description
        )
      )
      .finally(() => this.props.spinner(false));
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

LoginFormComponent.propTypes = {
  spinner: PropTypes.func.isRequired,
  message: PropTypes.func.isRequired,
  signIn: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    spinner: show => dispatch(toggleSpinner(show)),
    message: (severity, summary, detail) =>
      dispatch(showMessage(severity, summary, detail)),
    signIn: () => dispatch(changeLoginState(true))
  };
};

const LoginForm = connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginFormComponent);

export default LoginForm;
