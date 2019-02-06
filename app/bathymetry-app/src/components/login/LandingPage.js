import { ToggleButton } from "primereact/togglebutton";
import React, { Component } from 'react';
import './LandingPage.css';
import { LandingPageHeader } from './LandingPageHeader';
import { LoginForm } from './LoginForm';
import { RegisterForm } from './RegisterForm';
import UserService from "../../services/UserService";

export default class LandingPage extends Component {
    constructor(props) {
        super(props)

        this.state = {
            register: false
        }

        this.userService = new UserService();
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

    render() {
        return (
            <div className="loginControl">
                <div className="p-grid p-nogutter p-fluid" >
                    <LandingPageHeader />
                    <div className="p-col-1 p-lg-4"></div>
                    <div className="p-col p-lg-4">
                        {
                            this.state.register
                                ? <RegisterForm messageService={this.props.messageService} loadingService={this.props.loadingService} />
                                : <LoginForm signIn={this.props.signIn} messageService={this.props.messageService} loadingService={this.props.loadingService} />
                        }
                    </div>
                    <div className="p-col-1 p-lg-4"></div>
                    <div className="p-col-12" style={{ height: "20px" }} />

                    <div className="p-col-4" />
                    <div className="p-col-4">
                        <ToggleButton offLabel="Register" onLabel="Register" offIcon="pi pi-user-plus" onIcon="pi pi-user-plus"
                            checked={this.state.register} onChange={(e) => this.toggleRegisterForm(e.value)} />
                    </div>
                    <div className="p-col-4" />
                </div>
            </div>
        );
    }
}