import React, { Component } from 'react';
import Row from 'react-bootstrap/lib/Row';
import Col from 'react-bootstrap/lib/Col';
import Form from 'react-bootstrap/lib/Form';
import Button from 'react-bootstrap/lib/Button';
import Cookies from 'universal-cookie';

import {RestFetch} from './utility/Rest';


class LoginControl extends Component {
    constructor(props) {
        super(props);
        
        this.state = {
            username: '',
            password: '',
            register: false,
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.getToken = this.getToken.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        this.getToken();
    }

    getToken() {
        RestFetch.getLoginToken(this.state.username, this.state.password, this.props.changeLoginState.bind(null, true));
    }

    componentDidMount() {
        RestFetch.instantLogin(this.props.changeLoginState.bind(null, true));
    }

    render() {
        return(
            <div className="container-fluid">
                <Row style={{height: '100px'}}>
                </Row>
                <Row>
                    <Col xs={4}/>
                    <Col xs={4} className="border border-primary p-4">
                        <Form onSubmit={this.handleSubmit}>
                            <Form.Group controlId="formUsername">
                                <Form.Label>Username</Form.Label>
                                <Form.Control required type="text" placeholder="Enter username" name="username" value={this.state.username} onChange={this.handleChange}/>
                            </Form.Group>
                            
                            <Form.Group controlId="formPassword">
                                <Form.Label>Password</Form.Label>
                                <Form.Control required type="password" placeholder="Enter password" name="password" value={this.state.password} onChange={this.handleChange}/>
                            </Form.Group>

                            <Form.Group controlId="formCheckbox">
                                <Form.Check type="checkbox" label="Register" name="register" checked={this.state.register} onChange={this.handleChange}/>
                            </Form.Group>
                            <Button variant="primary" type="submit" >
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