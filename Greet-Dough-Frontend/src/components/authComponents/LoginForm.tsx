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
import {login} from "../../services/api";
import { withRouter } from 'react-router-dom';
import {toast, ToastContainer} from "react-toastify";

class LoginForm extends React.Component<any, any>{

    state = {
        email : "",
        password : "",
        invalid: false,
        redirect: null,
    }

    async loginWrapper( email:string, password:string ){
        let res = await login( email, password )
        const badLogin = () => toast.error("Incorrect login credentials!")

        if ( res === 200 ) {
            this.props.history.push('/');
        } else {
            this.setState( {invalid: true});
            badLogin();
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


