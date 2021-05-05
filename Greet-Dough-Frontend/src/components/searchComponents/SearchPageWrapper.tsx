import React from 'react'
import {
    Text,
    Box,
    Center,
    SkeletonCircle,
    HStack,
    Avatar,
    VStack,
    Divider,
} from "@chakra-ui/react";

import {
    Link
} from "react-router-dom";

import api from "../../services/api";
import {UserJson} from "../../services/types";

type SearchState = {
    name: string,
    users: UserJson[] | null,

}
class SearchPageWrapper extends React.Component<any, any> {

    state: SearchState = {
        name: "",
        users: null,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            name: props.name,
            users: null,
        }
    }

    componentDidMount() {
        api.searchUser(this.state.name)
            .then( users => {
                this.setState({users: users});
            });

    }

    render() {
        const users = this.state.users;
        const userTile = users?.map( (user) => (

            <>
                <Box w="400px"  h="100px"  marginBottom="40px">
                    <HStack>

                        <Box w="100px" h="100px">
                            <Avatar src={user.map.avatar} name={user.map.name} bg={"teal.400"} size={"xl"}/>
                        </Box>

                        <Box h="100px" w="300px" >
                            <VStack>
                                <Box w="100%" float={"left"}>
                                    <Link to={`/user/${user.map.ID}`}>
                                        <Text fontSize={"20px"} fontWeight={600}> {user.map.name} </Text>
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