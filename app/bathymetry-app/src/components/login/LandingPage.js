import { ToggleButton } from "primereact/togglebutton";
import React, { Component } from 'react';
import './LandingPage.css';
import { LandingPageHeader } from './LandingPageHeader';
import { LoginForm } from './LoginForm';
import { RegisterForm } from './RegisterForm';
import UserService from "../../services/UserService";
import { Button } from "primereact/button";

export default class LandingPage extends Component {
    constructor(props) {
        super(props)

        this.state = {
            register: false
        }

        this.userService = new UserService();

        this.loginAsGuest = this.loginAsGuest.bind(this)
        this.toggleRegisterForm = this.toggleRegisterForm.bind(this)
    }

    componentDidMount() {
        this.props.loadingService(true);
        this.userService.getUser()
            .then(response => {
                this.props.loadingService(false);
                this.props.signIn()
            })
            .catch(error => {
                this.props.loadingService(false)
            });
    }

    toggleRegisterForm(boolean) {
        this.setState({
            register: boolean
        });
    }

    loginAsGuest() {
        this.props.loadingService(true);
        this.userService.loginUser("read",  "read")
        .then(response => {
            this.props.loadingService(false);
            this.props.signIn();
        })
        .catch(error => {
            this.props.messageService("error", "Error", "failed to login");
        })
    }

    render() {
        return (
            <div className="loginControl">
                <div className="p-grid p-nogutter p-fluid" >
                    <LandingPageHeader />
                    <div className="p-col-4"></div>
                    <div className="p-col-4">
                        {
                            this.state.register
                                ? <RegisterForm toggleRegisterForm={this.toggleRegisterForm} messageService={this.props.messageService} loadingService={this.props.loadingService} />
                                : <LoginForm signIn={this.props.signIn} messageService={this.props.messageService} loadingService={this.props.loadingService} />
                        }
                    </div>
                    <div className="p-col-4"/>
                    <div className="p-col-12" style={{ height: "20px" }} />

                    <div className="p-col-4"/>
                    <div className="p-col-4">
                        <ToggleButton offLabel="Register" onLabel="Register" offIcon="pi pi-user-plus" onIcon="pi pi-user-plus"
                            checked={this.state.register} onChange={(e) => this.toggleRegisterForm(e.value)} />
                    </div>
                    <div className="p-col-4"/>

                    <div className="p-col-12" style={{ height: "20px" }} />

                    <div className="p-col-4"/>
                    <div className="p-col-4">
                        <Button label="Login as guest" onClick={this.loginAsGuest}/>
                    </div>
                    <div className="p-col-4"/>
                </div>
            </div>
        );
    }
}