import React from 'react'
import LoginForm from '../components/authComponents/LoginForm'
import {
    Text,
    Center,
    Divider,
} from "@chakra-ui/react";
import Header from "../components/globalComponents/Header";
function Login(){
    return (

        <>
            <Header />

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