import React, { Component } from 'react';
import Modal from 'react-bootstrap/lib/Modal';
import Button from 'react-bootstrap/lib/Button';

class DataModal extends Component {

    render() {
        return (
            <Modal show={this.props.show} onHide={this.props.close}>
                <Modal.Header closeButton>
                    <Modal.Title>Bathymetry Data</Modal.Title>
                </Modal.Header>
                <Modal.Body>Measure {this.props.measure}</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={this.props.close}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal>
        );
    }
}

export default DataModal;