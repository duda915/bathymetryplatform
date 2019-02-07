import React, { Component } from 'react'

import { NavLink } from 'react-router-dom'

export default class MenuButtonOnClick extends Component {

    render() {
        return (<div className="menu-button-container">
            <NavLink className="menu-button" onClick={this.props.onClick} exact to={this.props.to}>
                <div className="menu-button-content-container">
                    <div className="menu-button-icon"><i className={this.props.icon} /></div>
                    <div className="menu-button-label">{this.props.label}</div>
                </div>
            </NavLink>
        </div>);
    }

}  