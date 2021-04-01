import React from 'react';
import Header from '../components/Header'
import {
    Text,
    Center,
    Box,
    Button,
} from "@chakra-ui/react";
import HeaderChakra from "../components/HeaderChakra";

function Home(){
    return(

        <>
            <HeaderChakra/>

            <Center>
                <Text
                mt={10}
                // textDecoration={"underline orange solid"}
                textD
                fontSize={'6xl'}
                fontWeight={600}>
                This is a home page<span style={ {color:'orange', padding:'none'} }>!</span>
                </Text>
            </Center>

        </>

    );
}

export default Home;