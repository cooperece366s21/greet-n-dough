import React from "react"
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Divider,
    Button,
    Text,
    StackDivider,
    AlertDialog, AlertDialogOverlay, AlertDialogContent, AlertDialogHeader, AlertDialogBody, AlertDialogFooter,
} from "@chakra-ui/react";
import api from "../../services/api";
import {type} from "os";



type Feed = {
    posts: post[]
}

type post = {
    userID: number,
    imageID: number,
    title: string,
    contents: string,
    id: number,
    time: Time,
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
    hasOwnership: boolean,
    deleteAlert: boolean,
}

class UserFeed extends  React.Component<any, any> {

    state: FeedState = {
        cuid:-1,
        uid:-1,
        feed: null,
        hasOwnership: false,
        deleteAlert: false,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            cuid: props.cuid,
            uid: props.uid,
            feed: null,
            hasOwnership: props.hasOwnership,
            deleteAlert: false,
        }
    }

    refreshFeed() {
        api.getUserFeed( this.state.cuid, this.state.uid)
            .then( feed =>  {
                this.setState({feed: feed.reverse()});
            })
    }

    componentDidMount() {
        this.refreshFeed();
    }

    renderDeleteButton( pid:number ):any {
        if (this.state.hasOwnership) {
            return(
                <>
                    <Button
                        onClick={ () => this.setState({deleteAlert: true})}
                    >
                        🗑
                    </Button>

                    {/* Largely the same sample code as in Chakra examples*/}
                    <AlertDialog
                        isOpen={ this.state.deleteAlert }
                        onClose={ () => this.setState({deleteAlert: false})}
                        leastDestructiveRef={ undefined }>
                        <AlertDialogOverlay>
                            <AlertDialogContent>
                                <AlertDialogHeader fontSize="lg" fontWeight="bold">
                                    Delete Post
                                </AlertDialogHeader>

                                <AlertDialogBody>
                                    Are you sure you want to delete this post?
                                </AlertDialogBody>

                                <AlertDialogFooter>

                                    <Button onClick={ () => this.setState({deleteAlert: false})}>
                                        Cancel
                                    </Button>

                                    <Button colorScheme="red" ml={3}
                                            onClick={ () => {
                                                alert(pid);
                                                // api.deletePost(localStorage.getItem("authToken"), pid)
                                                //     .then( () => {
                                                //         this.refreshFeed();
                                                //         this.setState({deleteAlert: false})
                                                //     });
                                            }}
                                    >
                                        Delete
                                    </Button>

                                </AlertDialogFooter>
                            </AlertDialogContent>
                        </AlertDialogOverlay>
                    </AlertDialog>
                </>
            )
        } else {
            return( <> </> )
        }
    }

    render() {
        const feed = this.state.feed;


        const listFeed = feed?.map( (post) => (
            <>
                <Box w={"100%"}
                     padding={"20px"}
                     borderWidth={"1px"}
                     borderTopRadius={"15px"}
                     borderLeftRadius={"5px"}
                     borderRightRadius={"5px"}
                     borderBottomColor={"gray.500"}
                >

                    {/* Upper Region, TITLE and DELETE*/}
                    <HStack w={"100%"}>

                        <Box w="95%">
                            <Text fontSize={"30px"} fontWeight={600}> { post.title } </Text>
                        </Box>

                        <Box w="5%">
                            {this.state.hasOwnership &&
                                <Button
                                    onClick={ () => {
                                        api.deletePost(localStorage.getItem("authToken"), post.id)
                                            .then( () => this.refreshFeed() )
                                }}>
                                    🗑
                                </Button>
                            }
                        </Box>

                    </HStack>


                    {/* CONTENT field */}
                    <Box>
                        <Text fontSize={"20px"}> {post.contents} </Text>
                    </Box>

                    <HStack w={"100%"} marginTop={"10px"}>
                        <Box>
                            <Text color={"orange.400"}> Likes : </Text>
                        </Box>
                    </HStack>

                    {/*  LIKES/COMMENT button field */}
                    <HStack w={"100%"} marginTop={"10px"}>

                        <Box w={"50%"} >
                            <Text textAlign={"center"} cursor={"pointer"} borderRadius={"10px"}
                                  _hover={{
                                      background: "yellow.200",
                                  }}
                                  onClick={ () => api.addLike(localStorage.getItem("authToken"), post.id)}
                            >
                                👍 Like
                            </Text>
                        </Box>

                        <Box w={"50%"}>
                            <Text textAlign={"center"} cursor={"pointer"} borderRadius={"10px"}
                                  _hover={{
                                      background: "yellow.200",
                                  }}>
                                💬 Comment
                            </Text>
                        </Box>

                    </HStack>

                </Box>

                <Divider orientation={"horizontal"} w={"100%"} h={"20px"} opacity={0} />
            </>
        ))


        return (
            <Center w="100%" marginTop={"50px"}>

            <VStack w={"60%"} >
                { listFeed }
            </VStack>

            </Center>

        )
    }
}

export default UserFeed;