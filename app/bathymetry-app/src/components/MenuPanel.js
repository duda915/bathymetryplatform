import React, { Component } from 'react';

import {Menu} from 'primereact/menu';


class MenuPanel extends Component {
    constructor(props) {
        super(props);

        this.state = {
            menuItems: [
                {
                    label: 'Map',
                    items: [{label: 'Map', icon: 'pi pi-map-marker',command:()=>{ window.location.hash="/"; }}],
                }, 
                {
                    label: 'Data',
                    items: [{label: 'Select Data', icon: 'pi pi-search',command:()=>{ window.location.hash="/select"; }},
                            {label: 'My Data', icon: 'pi pi-cloud-upload',command:()=>{ window.location.hash="/mydata"; }}]
                },
                {
                    label: 'User',
                    items: [{label: 'Logout', icon: 'pi pi-sign-out',command:()=>{ this.props.logoutFun() }}],
                }
            ],
        }
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