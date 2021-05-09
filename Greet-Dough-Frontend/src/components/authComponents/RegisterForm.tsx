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
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import api, {register} from "../../services/api";
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

    async registerWrapper(email: string, username: string, password: string) {
        let res = await register(email, username, password);
        const alreadyExists = () => toast.error("Email already exists!");
        const otherErrors = () => toast.error("Backend Error: "+res);


        if ( res === 200 ) {
            alert("Registered!")
            this.props.history.push('/login');
        }

        else if ( res === 409 ){
            this.setState({invalid: true});
            return alreadyExists();
        }

        else {
            this.setState( {invalid: true} );
            return otherErrors();
        }
    }

    render() {
        return(

            <Center>

                <ToastContainer
                    position="bottom-right"
                    autoClose={5000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />

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
                                onClick={ () => this.registerWrapper(
                                    this.state.email,
                                    this.state.username,
                                    this.state.password,
                                ) }
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