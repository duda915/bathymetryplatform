import React, { Component } from 'react';

import {Panel} from 'primereact/panel';
import {InputText} from 'primereact/inputtext';
import {Password} from 'primereact/password';
import {Button} from 'primereact/button';

import 'primereact/resources/primereact.min.css';
import 'primereact/resources/themes/nova-colored/theme.css';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';
import {RestFetch} from './utility/Rest';


class LoginControl extends Component {
    constructor(props) {
        super(props);
        
        this.state = {
            username: '',
            password: '',
            register: false,
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.getToken = this.getToken.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        this.getToken();
    }

    getToken() {
        RestFetch.getLoginToken(this.state.username, this.state.password, this.props.changeLoginState.bind(null, true));
    }

    componentDidMount() {
        //instant login
        RestFetch.getUsername(this.props.changeLoginState.bind(null, true), null);
    }

    render() {
        return(
            <div className="loginControl">
                <div className="p-grid p-fluid" >
                
                    {/* first row */}
                    <div className="p-col-12" style={{height: '25vh'}}></div>

                    {/* login row */}
                    <div className="p-col-1 p-lg-4"></div>
                    <div className="p-col p-lg-4">
                        <Panel header="Login">
                            <form onSubmit={this.handleSubmit}>
                                <div className="p-grid">
                                    <div className="p-col">
                                        <div className="p-inputgroup">
                                            <span className="p-inputgroup-addon">
                                                <i className="pi pi-user"></i>
                                            </span>
                                            <InputText placeholder="Username" name="username" value={this.state.username} onChange={this.handleChange}></InputText>
                                        </div>
                                    </div>
                                </div>
                                <div className="p-grid">
                                    <div className="p-col">
                                        <div className="p-inputgroup">
                                            <span className="p-inputgroup-addon">
                                                <i className="pi pi-key"></i>
                                            </span>
                                            <Password placeholder="Enter password" feedback={false} name="password" value={this.state.password} onChange={this.handleChange}></Password>
                                        </div>
                                    </div>
                                </div>
                                <div className="p-grid p-fluid">
                                    <div className="p-col p-md-4"></div>
                                    <div className="p-col p-md-4"></div>
                                    <div className="p-col p-md-4">
                                        <Button label="Login" type="submit"/>
                                    </div>
                                </div>
                            </form>
                        </Panel>
                    </div>
                    <div className="p-col-1 p-lg-4"></div>

                </div>
            </div>
        );
    }
}
export default LoginControl;