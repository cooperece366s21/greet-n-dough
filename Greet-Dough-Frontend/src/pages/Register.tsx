import React from 'react'
import Header from '../components/Header'
import RegisterForm from "../components/RegisterForm";
import {Center, Text} from "@chakra-ui/react";
import HeaderChakra from "../components/HeaderChakra";

function Register(){
    return (

        <>
            <HeaderChakra />

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