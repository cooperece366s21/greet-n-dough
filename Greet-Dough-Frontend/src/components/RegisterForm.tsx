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

class RegisterForm extends React.Component<any, any>{
    render() {
        return(

            <Center>

                <Box w={'30%'} bg={'white'} >

                    <VStack>
                        <Input placeholder={'Username'} size={'lg'} errorBorderCOlor={'tomato'} />
                        <Input placeholder={'Password'} size={'lg'} errorBorderCOlor={'tomato'} />
                    </VStack>

                    <Box>
                        <HStack marginTop={10}>

                            <Button colorScheme={'yellow'} size={"md"} _hover={ {bg: 'yellow.500'} } >
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