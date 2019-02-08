import React, { Component } from 'react';

import { Menu } from 'primereact/menu';
import UserService from '../../../services/UserService';

import './MenuPanel.scss';
import { Button } from 'primereact/button';
import { ScrollPanel } from 'primereact/scrollpanel';

import { HashRouter } from 'react-router-dom'
import { NavLink } from 'react-router-dom'
import MenuButton from './MenuButton';
import MenuButtonOnClick from './MenuButtonOnClick';
import { ToggleButton } from 'primereact/togglebutton';
import Tools from './Tools';

class MenuPanel extends Component {
    constructor(props) {
        super(props);

        this.state = {
            menuItems: [
            ],
        }

        this.userService = new UserService();

        this.changeStyle = this.changeStyle.bind(this)
    }

    componentWillMount() {
        this.checkUser();
    }

    changeStyle() {
        this.props.changeStyle();
    }

    checkUser() {
        this.userService.getUser()
            .then(response => {
                // if(response.data.userRoles[0].role.roleName === "GUEST") {
                //     this.setState({
                //         isGuest: true
                //     });
                //     window.location.hash="/";
                // } else {
                //     this.setState({
                //         isGuest: false
                //     })
                // }
                this.generateMenu();
            });
    }

    generateMenu() {
        this.setState({
            menuItems: [
                {
                    label: '',
                    items: [{ label: 'Map', icon: 'pi pi-map-marker', command: () => { window.location.hash = "/"; } },
                    { label: 'Select Data', icon: 'pi pi-search', command: () => { window.location.hash = "/select"; } },
                    { label: 'My Data', disabled: this.state.isGuest, icon: 'pi pi-cloud-upload', command: () => { window.location.hash = "/mydata"; } },
                    { label: 'Settings', icon: 'pi pi-cog', command: () => { this.props.signOut() } },
                    { label: 'Logout', icon: 'pi pi-sign-out', command: () => { this.props.signOut() } }
                    ],
                },
                // {
                //     label: 'Data',
                //     items: [{ label: 'Select Data', icon: 'pi pi-search', command: () => { window.location.hash = "/select"; } },
                //     { label: 'My Data', disabled: this.state.isGuest, icon: 'pi pi-cloud-upload', command: () => { window.location.hash = "/mydata"; } }]
                // },
                // {
                //     label: 'User',
                //     items: [{ label: 'Logout', icon: 'pi pi-sign-out', command: () => { this.props.logoutFun() } }],
                // }
            ],
        });
    }

    render() {
        return (
            <div className="p-col-fixed menuslide-init menupanel">
                <div className="p-grid p-nogutter">
                    <div className="p-col-12 brand" />
                    <div className="p-col-12 accent-color" style={{ 'padding': '15px' }} >
                        <Tools signOut={this.props.signOut} changeStyle={this.changeStyle}/>
                        {/* <ToggleButton offLabel="Style" onLabel="Style"
                            checked={this.state.toggleStyleButton} onChange={this.changeStyle} onIcon='pi pi-eye' offIcon='pi pi-eye' /> */}
                    </div>

                    <div className="p-col-12 p-col-align-center">
                        {/* <Menu model={this.state.menuItems} className="menu" /> */}
                        <HashRouter>
                            <div className="router-container">
                                <div className="menu-top" />
                                <MenuButton to="/" label="Map" icon="pi pi-map-marker" />
                                <MenuButton to="/select" label="Select Data" icon="pi pi-search" />
                                <MenuButton to="/mydata" label="My Data" icon="pi pi-cloud-upload" />
                                <MenuButton to="/settings" label="Settings" icon="pi pi-cog" />
                                <MenuButtonOnClick to="/" label="Logout" icon="pi pi-sign-out" onClick={this.props.signOut} />
                            </div>
                        </HashRouter>
                    </div>



                </div>

            </div>
        );
    }


}

export default MenuPanel;