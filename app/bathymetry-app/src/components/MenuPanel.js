import React, { Component } from 'react';

import {Menu} from 'primereact/menu';
import UserService from '../services/UserService';



class MenuPanel extends Component {
    constructor(props) {
        super(props);

        this.state = {
            menuItems: [
            ],
        }

        this.userService = new UserService();
    }

    componentWillMount() {
        this.checkUser();
    }

    checkUser() {
        this.userService.getUser()
        .then(response => {
            if(response.data.userRoles[0].role.roleName === "GUEST") {
                this.setState({
                    isGuest: true
                });
                window.location.hash="/";
            } else {
                this.setState({
                    isGuest: false
                })
            }
            this.generateMenu();
        });
    }

    generateMenu() {
        this.setState({
            menuItems: [
                {
                    label: 'Map',
                    items: [{label: 'Map', icon: 'pi pi-map-marker',command:()=>{ window.location.hash="/"; }}],
                }, 
                {
                    label: 'Data',
                    items: [{label: 'Select Data', icon: 'pi pi-search',command:()=>{ window.location.hash="/select"; }},
                            {label: 'My Data', disabled: this.state.isGuest, icon: 'pi pi-cloud-upload',command:()=>{ window.location.hash="/mydata"; }}]
                },
                {
                    label: 'User',
                    items: [{label: 'Logout', icon: 'pi pi-sign-out',command:()=>{ this.props.logoutFun() }}],
                }
            ],
        });
    }

    render() {
        return(
            <div className="p-grid">
                <Menu model={this.state.menuItems} className="margin-center leftmenu-container"/>
            </div>
        );
    }


}

export default MenuPanel;