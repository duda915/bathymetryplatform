import React, { Component } from 'react';
import Alert from 'react-bootstrap/lib/Alert';
import Container from 'react-bootstrap/lib/Container';
import Row from 'react-bootstrap/lib/Row';
import Col from 'react-bootstrap/lib/Col';

class LoginControl extends Component {

    render() {
        return(
            <div className="container-fluid">
                <Row style={{height: '100px'}}>
                </Row>
                <Row>
                    <Col xs={4}/>
                    <Col xs={4}>
                        
                    </Col>
                    <Col xs={4}/>
                </Row>
            </div>
        );
    }
}
export default LoginControl;