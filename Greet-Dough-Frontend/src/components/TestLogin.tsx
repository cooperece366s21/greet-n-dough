import React from 'react';
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Text,
} from "@chakra-ui/react";
import api from "../services/api";
import { withRouter } from 'react-router-dom';

class testLogin extends React.Component<any, any> {

    state = {
        uid: -1
    }

    componentDidMount() {
        api.getUserID()
            .then( res => {
                this.setState( {uid: res});
            })
    }

    loggedIn() {
        return(
            <Box>
                <Text> Logged in as User {this.state.uid} </Text>
            </Box>
        )
    }

    notLoggedIn() {
        return(
            <Box>
                <text> User not logged in... </text>
            </Box>
        )
    }

    render() {
        const { uid } = this.state;

        switch ( uid ) {

            case null:
                return this.notLoggedIn();

            case -1:
                return this.notLoggedIn();

            default:
                return this.loggedIn();

        }

    }
}

export default testLogin
