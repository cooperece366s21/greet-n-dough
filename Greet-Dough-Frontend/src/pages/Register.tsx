import React from 'react'
import RegisterForm from "../components/authComponents/RegisterForm";
import {Center, Text} from "@chakra-ui/react";
import Header from "../components/globalComponents/Header";

function Register(){
    return (

        <>
            <Header />

            <Center>
                <Text
                    mt={10}
                    mb={10}
                    textD
                    fontSize={'6xl'}
                    fontWeight={600}>
                    Register
                </Text>
            </Center>

            <RegisterForm />
        </>

    );
}

export default Register;