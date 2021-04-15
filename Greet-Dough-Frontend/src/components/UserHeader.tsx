import React from 'react';
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Flex, Text, SkeletonCircle,
} from "@chakra-ui/react";
import {Link} from "react-router-dom";
import api from "../services/api";
import { withRouter } from 'react-router-dom';


type UserState = {
    uid: number;
    name: string;
    followers: number | null;
    posts: number | null;
    exists: boolean | null;
}

class UserHeader extends React.Component<any, any> {

    state: UserState = {
        uid: -1,
        name: "",
        followers:  null,
        posts: null,
        exists: null,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            uid: props.uid,
            name: "",
            followers: null,
            posts:null,
            exists: null,
        }
    }

    componentDidMount() {
        api.getUser(this.state.uid)
            .then( user => {
                if( user === 404 ) {
                    this.setState( {exists: false} );
                    return;
                }
                this.setState( {
                    name: user.name,
                    exists: true,
                });
            })

    }

    renderError() {
        return(
            <Center>
                <Text fontSize="4xl" color="red.500"> User does not exist! </Text>
            </Center>
        )
    }

    renderUser() {
         return(

            <Center marginTop="40px">

                 <Box w="175px" height="175px" borderWidth="2px" >
                     <SkeletonCircle size="175px" />
                 </Box>

                {/* RHS TEXT CONTAINER*/}
                <Box w="45%" height="175px" marginLeft={"20px"} borderWidth="2px">

                    {/*UPPER TEXT CONTAINER*/}
                    <Box w="100%" h="80px" >
                        <HStack>

                            <Text fontSize={'4xl'} fontWeight={500}> {this.state.name} </Text>

                            {/*<Text> FOLLOWERS </Text>*/}
                            {/*<Text> POSTS </Text>*/}


                        </HStack>
                    </Box>

                    {/*LOWER TEXT CONTAINER*/}
                    <Box w="80%" h="95px"  paddingTop="30px">
                        <Text> Lorem ipsum dolor sit amet, consectetur adipisicing elit. Doloribus eligendi minus nam sequi ullam. A ab alias commodi fugiat magni nobis non, perspiciatis quibusdam quis quos, ullam ut voluptas voluptatum?</Text>
                    </Box>

                </Box>

            </Center>

         )
     }


    render() {
        switch( this.state.exists ) {
            case null:
                return ( <> </> );
            case true:
                return this.renderUser();
            default:
                return this.renderError();
        }
    }



}

export default UserHeader;