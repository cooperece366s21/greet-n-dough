import React from "react"
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Text,
    StackDivider,
} from "@chakra-ui/react";
import api from "../../services/api";
import {type} from "os";


type Feed = {
    posts: post[]
}

type post = {
    userID: number,
    imageID: number,
    contents: string,
    time: Time,
    id: number,
}

type Time = {
    nano: number,
    year: number,
    monthValue: number,
    hour: number,
    minute: number,
    second: number,
    month: string,
    dayOfWeek: number,
    dayOfYear: number,
    chronology: Chronology,
}

type Chronology = {
    id: string, // ISO
    calendaryType: string // iso8601
}

type FeedState = {
    cuid: number,
    uid: number,
    feed: post[] | null,
}
class UserFeed extends  React.Component<any, any> {

    state: FeedState = {
        cuid:-1,
        uid:-1,
        feed: null,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            cuid: props.cuid,
            uid: props.uid,
            feed: null,
        }
    }

    componentDidMount() {
        api.getUserFeed( this.state.cuid, this.state.uid)
            .then( feed =>  {
                this.setState({feed: feed});

                // alert(  this.state.feed.map( (post) => (
                //     alert(post.contents)
                // ))
                // );
            })
    }

    render() {
        const feed = this.state.feed;
        const listFeed = feed?.map( (post) => (

            <Box w={"100%"}
                 background={"yellow.50"}
                 padding={"20px"}
                 borderWidth={"3px"}
                 borderRadius={"15px"}
                 borderColor={"gray.300"}>
                <Text fontSize={"30px"} fontWeight={600}> TITLE HERE </Text>
                <Text fontSize={"20px"}> {post.contents} </Text>
            </Box>
        ))


        return (
            <Center w="100%" marginTop={"50px"}>

            <VStack w={"70%"} >
                { listFeed }
            </VStack>

            </Center>

        )
    }
}

export default UserFeed;