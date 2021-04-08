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
import api, {register} from "../services/api"
import {isNull} from "util";

type RegisterState = {
    username: string;
    password: string;
};

class RegisterForm extends React.Component<any, any>{

    state = {
        email : "",
        username : "",
        password : "",
    }

    render() {
        // @ts-ignore
        return(

            <Center>

                <Box w={'30%'} bg={'white'} >

                    <VStack>

                        <Input placeholder={'E-mail'}
                               onChange={ e => this.setState( {email: e.target.value }) }
                               size={'lg'} errorBorderCOlor={'tomato'} id={'email'}
                        />

                        <Input placeholder={'Username'}
                               onChange={ e => this.setState( {username: e.target.value }) }
                               size={'lg'} errorBorderCOlor={'tomato'} id={'user'}
                        />
                        <Input placeholder={'Password'} value={this.state.password}
                               onChange={ e => this.setState( {password: e.target.value}) }
                               size={'lg'} errorBorderCOlor={'tomato'} id={'password'}
                        />
                    </VStack>

                    <Box>
                        <HStack marginTop={10}>

                            <Button colorScheme={'yellow'} size={"md"} _hover={ {bg: 'yellow.500'} }
                                onClick={ () => register(this.state.username) }
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

export default RegisterForm;