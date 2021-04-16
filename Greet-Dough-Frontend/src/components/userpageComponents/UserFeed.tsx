import React from "react"
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Text,
} from "@chakra-ui/react";


type FeedState = {
    uid: number,
}
class UserFeed extends  React.Component<any, any> {

    state: FeedState = {
        uid:-1,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            uid: props.uid,
        }
    }


    render() {
        return (
            <Text> Getting feed for {this.state.uid} </Text>
        )
    }
}

export default UserFeed;