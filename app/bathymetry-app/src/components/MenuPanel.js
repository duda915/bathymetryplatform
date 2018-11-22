import React, { Component } from 'react';

import {Menu} from 'primereact/menu';


class MenuPanel extends Component {
    constructor(props) {
        super(props);

        this.state = {
            menuItems: [
                {
                    label: 'Map',
                    items: [{label: 'Map', icon: 'pi pi-map-marker',command:()=>{ window.location.hash="/map"; }}],
                }, 
                {
                    label: 'Data',
                    items: [{label: 'Select Data', icon: 'pi pi-fw pi-cog',command:()=>{ window.location.hash="/select"; }},
                            {label: 'Add Data', icon: 'pi pi-fw pi-cloud-upload',command:()=>{ window.location.hash="/add"; }}]
                }
            ],
        }
    }

    render() {
        return(
            <div>
                <Menu model={this.state.menuItems} className="margin-center"/>
            </div>
        );
    }


}

export default MenuPanel;