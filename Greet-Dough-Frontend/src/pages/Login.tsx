import React from 'react'
import LoginForm from '../components/LoginForm'
import {
    Text,
    Center,
    Divider,
} from "@chakra-ui/react";
import HeaderChakra from "../components/HeaderChakra";
function Login(){
    return (

        <>
            <HeaderChakra />

            <Center>
                <Text
                    mt={10}
                    mb={10}
                    // textDecoration={"underline orange solid"}
                    textD
                    fontSize={'6xl'}
                    fontWeight={600}>
                    Login
                </Text>
            </Center>

            <LoginForm />
        </>

    );
}

export default Login;