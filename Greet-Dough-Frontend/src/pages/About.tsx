import React from 'react'
import Header from "../components/globalComponents/Header";
import {
    Box,
    Text,
    Divider,
    VStack,
    Center,
} from "@chakra-ui/react";
function About(){
    return (
        <>
            <Header/>
            <Box>
                <Center>
                    <VStack>
                        <Text fontWeight={700} fontSize={"36px"} >
                            Greet & Dough
                        </Text>

                        <Text> ECE366 - Software Engineering </Text>

                        <Text> The Cooper Union </Text>

                        <Text paddingBottom={"10px"}> Derek Lee, Thodori Kapouranis, Brian Doan, Steven Lee </Text>

                        <a href={"https://github.com/cooperece366s21/Lee-Ko"}>
                            <Text fontSize={"24px"} color={"cyan.700"} fontWeight={"500"}> Github URL</Text>
                        </a>

                    </VStack>

                </Center>
            </Box>
        </>

    );
}

export default About