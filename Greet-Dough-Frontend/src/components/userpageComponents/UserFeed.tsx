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
import {type} from "os";
import {PostObject} from "../../services/types";
import Comment from "./Comment";

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
    commentingNested: number,
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
        commentingNested: -1,

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
            commentingNested: -1,
        }
    }

    refreshFeed() {
        api.getUserFeed( this.state.cuid, this.state.uid)
            .then( feed =>  {
                this.setState({feed: feed});
            })
    }

    componentDidMount() {
        this.refreshFeed();
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
                            { e.map.images?.map(function(url:string, i){
                                return ( <Image src={url} /> )
                            })}
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
                                <Box borderWidth="1px" marginTop="10px" borderColor={"white"} >

                                    <Textarea
                                        marginTop="5px"
                                        placeholder={"type comment here"}
                                        onChange={ (e) => this.setState({comment: e.target.value})}

                                    />
                                    <Box h="20px"> </Box>

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
                            <Box height="40px"> </Box>

                            { e.map.comments.map((c, cIndex) =>{
                                return (
                                    <>

                                        <Box>
                                            <HStack color={"black"}>
                                                <Avatar name={c.map.username} bg="teal.500" src={c.map.avatar} w="50px"/>
                                                 <Text fontWeight={500} marginLeft={"10px"}> { c.map.username } </Text>
                                            </HStack>
                                        </Box>

                                        <HStack>

                                            <Box marginLeft="75px" w="75%" >
                                                <Text> {c.map.contents} </Text>
                                            </Box>

                                            { this.state.commentingNested === cIndex ?
                                                <>
                                                    <Button> ✔ </Button>
                                                    <Button onClick={ () => this.setState({commentingNested: -1})}>
                                                        ❌
                                                    </Button>
                                                </> :
                                                <Button onClick={ () => this.setState({ commentingNested: cIndex })}>
                                                    💬
                                                </Button>
                                            }


                                        </HStack>

                                        { this.state.commentingNested === cIndex ?
                                            <Textarea marginLeft="75px" w="75%" placeholder={"Type comment here..."} >

                                            </Textarea>

                                            : <> </>
                                        }

                                    </>
                                )
                            })}
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