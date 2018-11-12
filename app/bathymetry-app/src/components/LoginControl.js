import React, { Component } from 'react';
import Row from 'react-bootstrap/lib/Row';
import Col from 'react-bootstrap/lib/Col';
import Form from 'react-bootstrap/lib/Form';
import Button from 'react-bootstrap/lib/Button';

class LoginControl extends Component {

    render() {
        return(
            <div className="container-fluid">
                <Row style={{height: '100px'}}>
                </Row>
                <Row>
                    <Col xs={4}/>
                    <Col xs={4}>
                        <Form>
                            <Form.Group controlId="formUsername">
                                <Form.Label>Username</Form.Label>
                                <Form.Control type="text" placeholder="Enter username"/>
                            </Form.Group>
                            
                            <Form.Group controlId="formPassword">
                                <Form.Label>Password</Form.Label>
                                <Form.Control type="password" placeholder="Enter password"/>
                            </Form.Group>

                            <Form.Group controlId="formCheckbox">
                                <Form.Check type="checkbox" label="Register"/>
                            </Form.Group>
                            <Button variant="primary" type="submit">
                                Login
                            </Button>
                        </Form>
                    </Col>
                    <Col xs={4}/>
                </Row>
            </div>
        );
    }
}
export default LoginControl;