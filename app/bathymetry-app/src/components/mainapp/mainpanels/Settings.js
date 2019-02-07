
import React from 'react'
import { Panel } from 'primereact/panel';
import UserService from '../../../services/UserService';
import DataService from '../../../services/DataService';


export default class Settings extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            user: {}
        }

        this.userService = new UserService();
        this.dataService = new DataService();
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

    render() {

        return (
            <div className="bathymetry-app-padding">
                <Panel header="Account">
                    Username: {this.state.user.username} <br />
                    Email: {this.state.user.email} <br />
                    Uploaded datasets: {this.state.datasets} <br />
                    Authorities:
                    <ul>
                        {this.state.user.authorities}
                    </ul>
                </Panel>
            </div>

        )
    }
}