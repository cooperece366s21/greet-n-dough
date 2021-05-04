import React from 'react';
import {
    Text,
    Center,
    Box,
    Link,
    Button,
    HStack,
    Grid,
    SkeletonCircle,
    Stat, StatLabel, StatNumber, StatHelpText, StatArrow, StatGroup,
} from "@chakra-ui/react";
import Header from "../components/globalComponents/Header";

function Home(){
    return(

        <>
            <Header/>

            {/*Welcome message*/}
            {/*<Center>*/}
            {/*        <Text*/}
            {/*        mt={10}*/}
            {/*        // textDecoration={"underline orange solid"}*/}
            {/*        textD*/}
            {/*        fontSize={'6xl'}*/}
            {/*        fontWeight={600}>*/}
            {/*        This is a home page<span style={ {color:'orange', padding:'none'} }>!</span>*/}
            {/*        </Text>*/}

            {/*</Center>*/}

            {/*Images*/}

            <Center>
                <Box w={'70%'} bg={'sandybrown'} h={'500px'}>

                </Box>
            </Center>

            {/*Top users*/}
            <Center>
                <Box w={'70%'} mt={30}>
                    <Text fontSize={'2xl'} fontWeight={600} fontFamily={'Work Sans'} mb={5}>
                        Check out these users!
                    </Text>
                    <Grid templateColumns={'repeat(7,1fr)'} gap={6}>
                        <SkeletonCircle size={'20'} />
                        <SkeletonCircle size={'20'} />
                        <SkeletonCircle size={'20'} />
                        <SkeletonCircle size={'20'} />
                        <SkeletonCircle size={'20'} />
                        <SkeletonCircle size={'20'} />
                        <SkeletonCircle size={'20'} />
                    </Grid>

                </Box>
            </Center>

            {/*Stats*/}
            <Center>
                <Box w={'80%'} mt={'30px'}>
                    <HStack>

                        <Box w={'33%'}>
                            <Stat>
                                <Center> <StatLabel>Users Registered</StatLabel> </Center>
                                <Center> <StatNumber> 0 </StatNumber> </Center>
                                <StatHelpText>
                                    <Center>
                                        <StatArrow type={'increase'}/> 0 Today
                                    </Center>
                                </StatHelpText>
                            </Stat>
                        </Box>

                        <Box w={'33%'}>
                            <Stat>
                                <Center> <StatLabel>Posts Created</StatLabel> </Center>
                                <Center> <StatNumber> 0 </StatNumber> </Center>
                                <StatHelpText>
                                    <Center>
                                        <StatArrow type={'increase'}/> 0 Today
                                    </Center>
                                </StatHelpText>
                            </Stat>
                        </Box>

                        <Box w={'33%'}>
                            <Stat>
                                <Center> <StatLabel>Creators</StatLabel> </Center>
                                <Center> <StatNumber> 0 </StatNumber> </Center>
                                <StatHelpText>
                                    <Center>
                                        <StatArrow type={'increase'}/> 0 Today
                                    </Center>
                                </StatHelpText>
                            </Stat>
                        </Box>

                        <Stat>

                        </Stat>

                    </HStack>
                </Box>
            </Center>

            {/*Other infographic stuff*/}
            <Center>
                <Box w={'70%'} mt={30} mb={30}>
                    <Center>
                        <Link to={'/register'}>
                            <Button colorScheme={'yellow'} fontSize={'3xl'} paddingBlock={7} >
                                Register Today!
                            </Button>
                        </Link>

                    </Center>
                </Box>
            </Center>



        </>

    );
}

export default Home;