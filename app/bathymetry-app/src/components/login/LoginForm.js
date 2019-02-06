import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { Panel } from 'primereact/panel';
import { Password } from 'primereact/password';
import React from 'react';
import UserService from '../../services/UserService';

export class LoginForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: '',
        };

        this.userService = new UserService();

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);

    }

    componentDidMount() {
        this.props.loadingService(true);
        this.userService.getUser()
            .then(response => this.props.signIn())
            .catch(response => this.props.loadingService(false));
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
        this.props.loadingService(true);
        this.userService.loginUser(this.state.username, this.state.password)
            .then(response => {
                this.props.loadingService(false);
                this.props.signIn();
            })
            .catch(response => {
                console.log(response);
                this.props.loadingService(false);
                this.props.messageService("error", "Error", "wrong credentials");
            });
    }

    render() {

        return <Panel header="Login">
            <form onSubmit={this.handleSubmit}>
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
                                <i className="pi pi-key"></i>
                            </span>
                            <Password placeholder="Enter password" feedback={false} name="password" value={this.password} onChange={this.handleChange}></Password>
                        </div>
                    </div>
                    <div className="p-md-4 p-offset-8">
                        <Button label="Login" type="submit" />
                    </div>
                </div>
            </form >
        </Panel >;
    }

}
