import React from "react"
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Divider,
    Avatar,
    Textarea,
    Button,
    Text,
    Image,
    StackDivider,
    AlertDialog, AlertDialogOverlay, AlertDialogContent, AlertDialogHeader, AlertDialogBody, AlertDialogFooter,
} from "@chakra-ui/react";
import api from "../../services/api";
import {CommentObject} from "../../services/types";

class Comment extends React.Component<any, any> {

    state: CommentObject = {
        contents: "",
        ID: -1,
        postID: -1,
        avatar: "",
        userID: -1,
        username: "",
        reloadComments: false,
    }

    constructor(props: any) {
        super(props);
        this.state = {
            contents: props.contents,
            ID: props.ID,
            postID: props.postID,
            avatar: props.avatar,
            userID: props.userID,
            username: props.username,
            reloadComments: props.reloadComments,
        }
    }

    renderComment() {
        alert("I am being rendered");
        return (
            <>
                <Box>
                    <HStack color={"black"}>
                        <Avatar name={this.state.username} bg="teal.500" src={this.state.avatar} w="50px"/>
                        <Text fontWeight={500}> {this.state.username} </Text>
                    </HStack>
                </Box>

                <HStack>

                    <Box marginLeft="75px" w="75%" bg={"cyan.200"}>
                        <Text> {this.state.contents} </Text>
                    </Box>
                    <Button>
                        💬
                    </Button>

                </HStack>

            </>
        )
    }

    render(){
        return this.state.reloadComments ? (<> </>) : this.renderComment();
    }

}

export default Comment