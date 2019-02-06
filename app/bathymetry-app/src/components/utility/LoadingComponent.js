import React, { Component } from 'react';
import { ProgressSpinner } from 'primereact/progressspinner';

export default class LoadingComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
            show: false,
        }
    }

    showLoading(show) {
        this.setState({
            show: show,
        })
    };


    render() {
        return (
            <div>
                {this.state.show ?
                    <div className="loading-container ">
                        <ProgressSpinner className="loading-progress" />
                    </div>
                    : null}
            </div>
        )
    }
}