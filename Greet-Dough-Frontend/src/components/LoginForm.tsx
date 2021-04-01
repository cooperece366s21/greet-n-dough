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

class LoginForm extends React.Component<any, any>{

    render() {
        return(

            <Center>

                <Box w={'30%'} bg={'white'} >

                    <VStack>
                        <Input placeholder={'Username'} size={'lg'} errorBorderCOlor={'tomato'} />
                        <Input placeholder={'Password'} size={'lg'} errorBorderCOlor={'tomato'} />
                    </VStack>

                        <Box float={'left'}>
                            <HStack marginTop={10}>

                                <Button colorScheme={'yellow'} size={"md"} _hover={ {bg: 'yellow.500'} } >
                                    Login
                                </Button>

                            </HStack>
                        </Box>

                    <Box float={'right'}>
                        <HStack marginTop={10}>
                            <Link to="/register">
                                <Button colorScheme={'yellow'} size={"md"} _hover={ {bg: 'yellow.500'} } >
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

export default LoginForm;


