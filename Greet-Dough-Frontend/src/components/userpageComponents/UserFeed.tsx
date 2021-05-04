import React from "react"
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Divider,
    Textarea,
    Button,
    Text,
    Image,
    StackDivider,
    AlertDialog, AlertDialogOverlay, AlertDialogContent, AlertDialogHeader, AlertDialogBody, AlertDialogFooter,
} from "@chakra-ui/react";
import api from "../../services/api";
import {type} from "os";
import {PostObject} from "../../services/types";

type Entry = {
    map: PostObject,
}

type FeedState = {
    cuid: number,
    uid: number,
    feed: Entry[] | null,
    hasOwnership: boolean,
    deleteAlert: boolean,
    openComments: number, // number indicates what post id to open comments for
    comment: string,
    commentingOnPost: boolean,
}

class UserFeed extends  React.Component<any, any> {

    state: FeedState = {
        cuid:-1,
        uid:-1,
        feed: null,
        hasOwnership: false,
        deleteAlert: false,
        openComments: -1,
        comment: "",
        commentingOnPost: false,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            cuid: props.cuid,
            uid: props.uid,
            feed: null,
            hasOwnership: props.hasOwnership,
            deleteAlert: false,
            openComments: -1,
            comment: "",
            commentingOnPost: false,
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


        const listFeed = feed?.map( (e,k) => (
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

                        <Box w="90%">
                            <Text fontSize={"30px"} fontWeight={600}> { e.map.post.title } </Text>
                        </Box>

                        <Box w="5%">
                            {this.state.hasOwnership &&
                            <Button
                                onClick={()=> window.location.replace("/edit/" + e.map.post.ID.toString() ) }>
                                ✏
                            </Button>
                            }
                        </Box>

                        <Box w="5%">
                            {this.state.hasOwnership &&
                                <Button
                                    onClick={ () => {
                                        api.deletePost(localStorage.getItem("authToken"), e.map.post.ID)
                                            .then( () => this.refreshFeed() )
                                }}>
                                    🗑
                                </Button>
                            }
                        </Box>

                    </HStack>


                    {/* CONTENT field */}

                    <Box>
                        <Center>
                            { e.map.images[0] ? <Image src={e.map.images[0]}/> : <></>}
                        </Center>

                        <Text fontSize={"20px"}> {e.map.post.contents}  </Text>
                    </Box>

                    <HStack w={"100%"} marginTop={"10px"}>
                        <Box>
                            <Text color={"orange.400"}> Likes : { e.map.likeCount } </Text>
                        </Box>
                    </HStack>

                    {/*  LIKES/COMMENT button field */}
                    <HStack w={"100%"} marginTop={"10px"}>

                        <Box w={"50%"} >
                            <Text textAlign={"center"} cursor={"pointer"} borderRadius={"10px"}
                                  _hover={{
                                      background: "yellow.200",
                                  }}
                                  onClick={ () => api.addLike(localStorage.getItem("authToken"), e.map.post.ID)
                                      .then( () => this.refreshFeed() )
                                  }
                            >
                                👍 Like
                            </Text>
                        </Box>

                        <Box w={"50%"}>
                            <Text textAlign={"center"} cursor={"pointer"} borderRadius={"10px"}
                                  _hover={{background: "yellow.200"}}
                                  onClick={ () => {
                                      if ( this.state.openComments === k ) {
                                          this.setState({openComments: -1})
                                      } else {
                                          this.setState({openComments: k});
                                      }

                                  }}
                            >
                                💬 Open comments
                            </Text>
                        </Box>

                    </HStack>

                    {/* COMMENT field*/}
                    { this.state.openComments === k ?
                        <>
                            {this.state.commentingOnPost ?
                                <Box borderWidth="1px" marginTop="10px"  borderTopColor="gray.300">

                                    <Textarea
                                        marginTop="5px"
                                        placeholder={"type comment here"}
                                        onChange={ (e) => this.setState({comment: e.target.value})}

                                    />

                                    <Box w="100%">

                                        <Button float="right" colorScheme="red" marginLeft="15px"
                                                onClick={() => this.setState({commentingOnPost: false}) } >
                                            Cancel
                                        </Button>

                                        <Button float="right" colorScheme="green"
                                                onClick={() => {
                                                    api.makeComment(
                                                        localStorage.getItem("authToken"),
                                                        e.map.post.ID,
                                                        this.state.comment,
                                                        null,
                                                    ).then( () => this.refreshFeed() );
                                                }}>
                                            Comment
                                        </Button>

                                    </Box>

                                </Box> :
                                <Center>
                                    <Button marginTop="10px"
                                            onClick={ () => this.setState({commentingOnPost: true})}>
                                        Comment on post
                                    </Button>
                                </Center>
                            }
                        </> : <> </>
                    }
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