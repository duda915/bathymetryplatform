import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Panel } from "primereact/panel";
import { Password } from "primereact/password";
import PropTypes from "prop-types";
import React from "react";
import { connect } from "react-redux";
import API from "../../../services/API";
import { saveTokens } from "../../../services/Token";
import { handleRequest } from "../../utility/requesthandler";
import { changeLoginState } from "../LoginActions";

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

    const api = new API();

    handleRequest({
      requestPromise: api
        .restUser()
        .loginUser(this.state.username, this.state.password),
      onSuccess: response => {
        saveTokens(response);
        this.props.signIn();
      },
      onErrorMessage: error => error.response.data.error_description
    });
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
  signIn: PropTypes.func.isRequired
};

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    signIn: () => dispatch(changeLoginState(true))
  };
};

const LoginForm = connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginFormComponent);

export default LoginForm;
