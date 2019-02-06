
import React, { Component } from 'react'
import { Panel } from 'primereact/panel';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';

export class RegisterForm extends Component {
  constructor(props) {
    super(props);
    this.state = ({
      username: '',
      password: '',
      confirmPassword: '',
      email: ''
    });

    this.onSubmit = this.onSubmit.bind(this)
    this.handleChange = this.handleChange.bind(this)
  }

  handleChange(event) {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;

    this.setState({
      [name]: value
    });
  }

  onSubmit(event) {
    event.preventDefault();

    this.checkIfPasswordsMatch();
  }

  checkIfPasswordsMatch() {
    if (this.state.password !== this.state.confirmPassword) {
      this.props.messageService("warn", "Error", "passwords are not the same");
    }
  }

  render() {
    return (
      <Panel header="Register">
        <form onSubmit={this.onSubmit}>
          <div className="p-grid">
  
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-user"></i>
                </span>
                <InputText autoComplete="off" placeholder="Username" name="username" value={this.username} onChange={this.handleChange}></InputText>
              </div>
            </div>
            
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-home"></i>
                </span>
                <InputText autoComplete="off" placeholder="Email" name="email" value={this.email} onChange={this.handleChange}></InputText>
              </div>
            </div>

            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-key"></i>
                </span>
                <Password feedback={false} autoComplete="off" placeholder="Enter password" name="password" value={this.password} onChange={this.handleChange}></Password>
              </div>
            </div>
  
            <div className="p-col-12">
              <div className="p-inputgroup">
                <span className="p-inputgroup-addon">
                  <i className="pi pi-key"></i>
                </span>
                <Password feedback={false} autoComplete="off" placeholder="Confirm password" name="confirmPassword" value={this.confirmPassword} onChange={this.handleChange}></Password>
              </div>
            </div>

            <div className="p-col-4 p-offset-8">
              <Button type="submit" label="Register"/>
            </div>

          </div>
        </form>
      </Panel>
    );
  }
}  