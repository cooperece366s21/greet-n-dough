import React from 'react';
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Flex, Text, SkeletonCircle,
} from "@chakra-ui/react";
import {Link} from "react-router-dom";
import api from "../services/api";
import { withRouter } from 'react-router-dom';

type UserState = {
    uid: number | null;
    name: string;
}

class UserChakra extends React.Component<any, any> {

    state: UserState = {
        uid: null,
        name: "",
    };

    componentDidMount() {
        api.getUserID()
            .then( uid => {
                this.setState( {uid: uid});

                if (uid!==-1) {
                    api.getUser(uid).then( user => {
                            this.setState( {name: user.name} );
                    })
                }
            })
    }

    loggedIn() {
        return(
            <HStack marginLeft="80px">

            {/*User Icon*/}
                <SkeletonCircle size='60px' />

            {/*Greeting and logout V-stack*/}
                <VStack>
                    <Box> Welcome, {this.state.name}! </Box>
                    <Button colorScheme={'yellow'} size={"sm"}
                            _hover={{bg: 'yellow.500'}} onClick={api.logout}>
                        Logout
                    </Button>
                </VStack>

            </HStack>
        )
    }

    notLoggedIn() {

        return(

            <HStack marginLeft="120px">

                <Link to="/login">
                    <Box borderWidth={1} padding={2} borderColor={"black"} marginRight={3}
                         _hover={{ background:"orange.300"}}>
                        SIGN IN
                    </Box>
                </Link>

                <Link to="/register">
                    <Box borderWidth={1} padding={2} borderColor={"black"} marginRight={3}
                         _hover={{ background:"orange.300"}}>
                        REGISTER
                    </Box>
                </Link>

            </HStack>
        )
    }

    render() {
        const { uid } = this.state;

        switch ( uid ) {

            case null:
                // Waiting on API ( default state )
                return ( <> </> );

            case -1:
                // Explicit -1 returned from API
                return this.notLoggedIn();

            default:
                // UID returned from API
                return this.loggedIn();

        }
    }
}

export default UserChakra