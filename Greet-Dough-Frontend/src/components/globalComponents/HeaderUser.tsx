import React from 'react';
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Flex,
    Text,
    SkeletonCircle,
    Drawer,
    DrawerBody,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton, Avatar,
} from "@chakra-ui/react";
import {Link} from "react-router-dom";
import api from "../../services/api";
import { withRouter } from 'react-router-dom';
import {User} from "../../services/types";

type UserState = {
    user: User|null,
    loggedIn: boolean|null,
    drawer: boolean;
    wallet : string | null;
    addAmount : string | null;
}

class HeaderUser extends React.Component<any, any> {

    state: UserState = {
        user: null,
        loggedIn: null,
        drawer: false,
        wallet: "0",
        addAmount: "",
    };

    componentDidMount() {
        api.getCurrentUserID()
            .then( uid => {

                if (uid!==-1) {

                    api.getUser(uid)
                        .then( user => {
                            this.setState( {user: user} );
                            this.setState({loggedIn: true } );
                        })

                    api.getWallet( localStorage.getItem('authToken') )
                        .then( body => {
                            this.setState( { wallet: body})
                        })
                } else{
                    this.setState({loggedIn:false})
                }
            });

    }

    loggedIn() {
        return(
            <HStack marginLeft="80px">

            {/*User Icon*/}
            <Link to={`/user/${this.state.user?.ID}`}>
                <Avatar name={this.state.user?.name} bg="teal.500" src={this.state.user?.avatar} w="50px"/>
            </Link>

            {/*Greeting and logout V-stack*/}
                <VStack>
                    <Box>{this.state.user?.name} </Box>
                    <Button colorScheme={'yellow'} size={"sm"}
                            _hover={{bg: 'yellow.500'}} onClick={api.logout}>
                        Logout
                    </Button>
                </VStack>

                <Button background="yellow.300" onClick={ () => this.setState({drawer: true})} >
                    ⚙
                </Button>

                {/* SETTING DRAWER */}
                <Drawer
                    isOpen={this.state.drawer}
                    placement="right"
                    onClose={ () => this.setState({drawer:false})}
                >
                    <DrawerOverlay>
                        <DrawerContent>
                            <DrawerCloseButton />

                            <DrawerHeader>Wallet:
                                <span color="green.200"> ${this.state.wallet} </span>
                            </DrawerHeader>

                            <DrawerBody>
                                <Input placeholder="Add money amount..."
                                       onChange={ e => this.setState( {addAmount: e.target.value}) }
                                />
                                <Button colorScheme={"green"}
                                        marginTop={"10px"}
                                        float={"right"}
                                        onClick={ () =>  {
                                            let token = localStorage.getItem("authToken");
                                            api.addToWallet( token, this.state.addAmount)
                                                .then( () => api.getWallet(token)
                                                    .then( newMoney => this.setState({wallet: newMoney}) ) )
                                        }}
                                > Add money </Button>
                            </DrawerBody>

                            <DrawerFooter>
                                <Button variant="outline" mr={3} onClick={ () => this.setState( {drawer: false})}>
                                    Cancel
                                </Button>
                            </DrawerFooter>
                        </DrawerContent>
                    </DrawerOverlay>

                </Drawer>

            </HStack>
        )
    }

    notLoggedIn() {

        return(

            <HStack marginLeft="120px">

                <Link to="/login">
                    <Box borderWidth={1} padding={2} borderColor={"black"} marginRight={3} borderRadius="10px"
                         _hover={{ background:"orange.300"}}>
                        SIGN IN
                    </Box>
                </Link>

                <Link to="/register">
                    <Box borderWidth={1} padding={2} borderColor={"black"} marginRight={3} borderRadius="10px"
                         _hover={{ background:"orange.300"}}>
                        REGISTER
                    </Box>
                </Link>

            </HStack>
        )
    }

    render() {
        let uid  = this.state.loggedIn;

        switch ( uid ) {

            case null:
                // Waiting on API ( default state )
                return (<> </>);

            case false:
                // Explicit -1 returned from API
                return this.notLoggedIn();

            default:
                // UID returned from API
                return this.loggedIn();

        }
    }
}

export default HeaderUser