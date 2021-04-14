import './LoginForm.css';
import React from 'react';
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Flex,
} from "@chakra-ui/react";
import {Link} from "react-router-dom";
import {login} from "../services/api";
import { withRouter } from 'react-router-dom';

class LoginForm extends React.Component<any, any>{

    state = {
        email : "",
        password : "",
        invalid: false,
        redirect: null,
    }

    async loginWrapper( email:string, password:string ){
        let res = await login( email, password )
        if ( res === 200 ) {
            // alert("Successful log in");
            this.props.history.push('/');
        } else {
            alert("Response: " + JSON.stringify(res) );
            this.setState( {invalid: true});
        }
    }

    render() {
        return(

            <Center>

                <Box w={'30%'} bg={'white'} >

                    <VStack>

                        <Input placeholder={'Email'}
                               onChange={ e => this.setState( {email: e.target.value }) }
                               isInvalid={this.state.invalid}
                               size={'lg'}
                               errorBorderCOlor={'tomato'} />

                        <Input placeholder={'Password'}
                               onChange={ e => this.setState( {password: e.target.value }) }
                               isInvalid={this.state.invalid}
                               size={'lg'}
                               errorBorderCOlor={'tomato'} />

                    </VStack>

                        <Box float={'left'}>
                            <HStack marginTop={10}>

                                <Button colorScheme={'yellow'}
                                        size={"md"}
                                        _hover={ {bg: 'yellow.500'} }
                                        onClick={() => this.loginWrapper( this.state.email, this.state.password)}
                                >
                                    Login
                                </Button>

                            </HStack>
                        </Box>

                    <Box float={'right'}>
                        <HStack marginTop={10}>
                            <Link to="/register">
                                <Button colorScheme={'yellow'} size={"md"} _hover={ {bg: 'yellow.500'} }>
                                    Not Registered?
                                </Button>
                            </Link>
                        </HStack>
                    </Box>



                </Box>

            </Center>
        )
    }

}

export default withRouter(LoginForm);


