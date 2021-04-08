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
import api, {register} from "../services/api";
import { withRouter } from 'react-router-dom';

type RegisterState = {
    email: string;
    username: string;
    password: string;
    hasError: boolean;
};

class RegisterForm extends React.Component<any, any>{

    state = {
        email : "",
        username : "",
        password : "",
        invalid: false,
        redirect: null,
    }

    registerWrapper( username:string ){
        let res = register( username )
        if ( res ) {
            alert("Registered!\nRedirecting to login page...");
            this.props.history.push('/login');
        }
        else {
            alert("oopsie!");
            this.setState( {invalid: true});
        }
    }

    render() {

        return(

            <Center>

                <Box w={'30%'} bg={'white'} >

                    <VStack>

                        <Input placeholder={'E-mail'}
                               onChange={ e => this.setState( {email: e.target.value }) }
                               isInvalid={this.state.invalid}
                               size={'lg'} errorBorderCOlor={'tomato'} id={'email'}
                        />

                        <Input placeholder={'Username'}
                               onChange={ e => this.setState( {username: e.target.value }) }
                               isInvalid={this.state.invalid}
                               size={'lg'} errorBorderCOlor={'tomato'} id={'user'}
                        />
                        <Input placeholder={'Password'} value={this.state.password}
                               onChange={ e => this.setState( {password: e.target.value}) }
                               isInvalid={this.state.invalid}
                               size={'lg'} errorBorderCOlor={'tomato'} id={'password'}
                        />
                    </VStack>

                    <Box>
                        <HStack marginTop={10}>

                            <Button colorScheme={'yellow'} size={"md"} _hover={ {bg: 'yellow.500'} }
                                onClick={ () => this.registerWrapper(this.state.username) }
                            >
                                Register
                            </Button>

                        </HStack>

                    </Box>
                    
                </Box>

            </Center>
        )
    }
}


export default withRouter(RegisterForm);