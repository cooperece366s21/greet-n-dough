import React from 'react'
import {
    Text,
    Box,
    Center,
    SkeletonCircle,
    HStack,
    VStack,
    Divider,
} from "@chakra-ui/react";

import {
    Link
} from "react-router-dom";

import api from "../../services/api";


type User = {
    name: string,
    id: string,
}

type SearchState = {
    name: string,
    results: User[] | null,

}
class SearchPageWrapper extends React.Component<any, any> {

    state: SearchState = {
        name: "",
        results: null,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            name: props.name,
            results: null,
        }
    }

    componentDidMount() {
        api.searchUser(this.state.name)
            .then( users => {
                this.setState({results: users});
            });

    }

    render() {
        const users = this.state.results;
        const userTile = users?.map( (user) => (

            <>
                <Box w="400px"  h="100px"  marginBottom="40px">
                    <HStack>

                        <Box w="100px" h="100px">
                            <SkeletonCircle size="95px" />
                        </Box>

                        <Box h="100px" w="300px" >
                            <VStack>
                                <Box w="100%" float={"left"}>
                                    <Link to={`/user/${user.id}`}>
                                        <Text fontSize={"20px"} fontWeight={600}> {user.name} </Text>
                                    </Link>
                                </Box>
                            </VStack>
                        </Box>

                    </HStack>
                </Box>
            </>
        ))

        return (
            <>
                <Center>
                    <Box w="70%">
                        <Text fontSize="24px" marginBottom="30px">
                            Search results for : {this.state.name}
                        </Text>
                        { userTile }
                    </Box>
                </Center>

            </>
        )

    }

}

export default SearchPageWrapper;