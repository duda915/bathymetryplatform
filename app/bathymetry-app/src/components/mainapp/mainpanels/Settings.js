
import React from 'react'
import { Panel } from 'primereact/panel';
import UserService from '../../../services/UserService';
import DataService from '../../../services/DataService';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import PasswordDTO from '../../../services/dtos/PasswordDTO';


export default class Settings extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            user: {},
            password: '',
            newPassword: '',
            confirmNewPassword: '',
        }

        this.userService = new UserService();
        this.dataService = new DataService();

        this.handleSubmit = this.handleSubmit.bind(this)
        this.handleChange = this.handleChange.bind(this)
    }

    componentDidMount() {
        this.fetchUserData();
        this.fetchDataSets();
    }

    fetchUserData() {
        this.userService.getUser()
            .then(response => {
                this.setState({
                    user: {
                        username: response.data.username,
                        email: response.data.email,
                        authorities: response.data.userAuthorities.map(authority =>
                            <li key={authority.authority.id}>{authority.authority.authorityName}</li>
                        )
                    }
                })
            });
    }

    fetchDataSets() {
        this.dataService.getUserDataSets()
            .then(response => this.setState({ datasets: response.data.length }));
    }

    handleSubmit(event) {
        event.preventDefault();

        const username = this.state.user.username;
        const password = this.state.password;
        const newPassword = this.state.newPassword;
        const confirmNewPassword = this.state.confirmNewPassword;

        this.userService.loginUser(username, password)
            .then(response => {
                if (newPassword == confirmNewPassword) {
                    const passwordDTO = new PasswordDTO(newPassword);

                    this.props.loadingService(true);

                    this.userService.changePassword(passwordDTO)
                        .then(response => {
                            this.props.messageService('success', "Success", 'password changed successfully');
                        })
                        .catch(error => {
                            this.props.messageService('error', 'Error', error.response.data.message);
                        }).finally(this.props.loadingService(false));
                } else {
                    this.props.messageService('error', 'Error', 'new passwords are not matching');
                }
            })
            .catch(error => {
                this.props.messageService('error', 'Error', 'old password is incorrect');
            })

        this.setState({
            password: '',
            newPassword: '',
            confirmNewPassword: ''
        })

    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    render() {

        return (
            <div className="bathymetry-app-padding">
                <Panel header="Account">
                    <div className="p-grid">
                        <div className="p-col-4">
                            <Panel header="Info">
                                Username: {this.state.user.username} <br />
                                Email: {this.state.user.email} <br />
                                Uploaded datasets: {this.state.datasets} <br />
                                Authorities:
                                <ul>
                                    {this.state.user.authorities}
                                </ul>
                            </Panel>
                        </div>

                        <div className="p-col-4">
                            <Panel header="Change Password">
                                <div className="p-grid p-nogutter p-justify-center">
                                    <form onSubmit={this.handleSubmit}>
                                        <div className="p-col-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon">
                                                    <i className="pi pi-key"></i>
                                                </span>
                                                <Password placeholder="Current password" feedback={false} name="password"
                                                    value={this.state.password} onChange={this.handleChange} />
                                            </div>
                                        </div>

                                        <div className="p-col-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon">
                                                    <i className="pi pi-key"></i>
                                                </span>
                                                <Password placeholder="New password" feedback={false} name="newPassword"
                                                    value={this.state.newPassword} onChange={this.handleChange} />
                                            </div>
                                        </div>

                                        <div className="p-col-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon">
                                                    <i className="pi pi-key"></i>
                                                </span>
                                                <Password placeholder="Confirm new password" feedback={false} name="confirmNewPassword"
                                                    value={this.state.confirmNewPassword} onChange={this.handleChange} />
                                            </div>
                                        </div>

                                        <div className="p-col-12" >
                                            <Button label="Change password" type="submit" style={{ 'width': '100%' }} />
                                        </div>
                                    </form>
                                </div>
                            </Panel>
                        </div>

                    </div>


                </Panel>
            </div>

        )
    }
}