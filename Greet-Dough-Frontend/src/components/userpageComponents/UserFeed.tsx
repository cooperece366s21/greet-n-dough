import React from "react"
import {
    Box,
    VStack,
    HStack,
    Center,
    Divider,
    Avatar,
    Textarea,
    Button,
    Text,
    Spinner,
    Image,
} from "@chakra-ui/react";
import api from "../../services/api";
import { Carousel } from 'react-responsive-carousel';
import "react-responsive-carousel/lib/styles/carousel.min.css";
import {CommentJson, CommentObject, PostJson, PostObject} from "../../services/types";
import {toast, ToastContainer} from "react-toastify";

type FeedState = {
    cuid: number,
    uid: number,
    feed: PostJson[] | null,
    hasOwnership: boolean,
    deleteAlert: boolean,
    openComments: number, // number indicates what post id to open comments for
    comment: string,
    commentingOnPost: boolean,
    commentingNested: number,
    initiallyLoaded: false,
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
        initiallyLoaded: false,

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
            initiallyLoaded:false,
        }
    }

    async refreshFeed() {
        api.getUserFeed( this.state.cuid, this.state.uid)
            .then( feed =>  {
                this.setState({
                    feed: feed,
                    initiallyLoaded: true,
                });
            })
    }

    componentDidMount() {
        this.refreshFeed()
    }

    render() {
        const feed = this.state.feed;

        const listFeed = feed?.map( (post:PostJson,k) => (
            <>
                <Box w={"100%"}
                     id={"post" + k}
                     padding={"20px"}
                     borderWidth={"1px"}
                     borderTopRadius={"15px"}
                     borderLeftRadius={"5px"}
                     borderRightRadius={"5px"}
                     borderBottomColor={"gray.500"}
                >

                    {this.renderPostHeader(post)}

                    {this.renderPostContent(post)}

                    {this.renderInteractionButtons(post, k)}

                    {this.renderContentField(k, post)}

                </Box>
                <Divider orientation={"horizontal"} w={"100%"} h={"20px"} opacity={0}/>
            </>
        ))


        return (
            <>
                <ToastContainer
                    position="bottom-right"
                    autoClose={5000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />

                <Center w="100%" marginTop={"50px"}>
                    <VStack w={"60%"}>
                        { !this.state.initiallyLoaded && <Spinner color={"blue.400"} size={"xl"} /> }
                        { listFeed }
                    </VStack>
                </Center>
            </>

        )
    }

    private renderPostHeader(post: PostJson) {
        const deleteGood = () => toast.success("Post deleted!",
            { style:{ backgroundColor:"#5da356"} }
        );

        const deleteBad = () => toast.error("Could not delete post!");

        return <HStack w={"100%"}>

            <Box w="90%">
                <Text fontSize={"30px"} fontWeight={600}> {post.map.post.title} </Text>
            </Box>

            <Box w="5%">
                {this.state.hasOwnership &&
                <Button
                    onClick={() => window.location.replace("/edit/" + post.map.post.ID.toString())}>
                    ✏
                </Button>
                }
            </Box>

            <Box w={"10px"}/>

            <Box w="5%" paddingRight={"20px"}>
                {this.state.hasOwnership &&
                <Button
                    onClick={() => {
                        api.deletePost(post.map.post.ID)
                            .then( (res) => {
                                if (res===200) {
                                    deleteGood();
                                    this.refreshFeed();
                                } else { deleteBad() }
                            })
                    }}>
                    🗑
                </Button>
                }
            </Box>

        </HStack>;
    }

    private renderInteractionButtons(post: PostJson, k: number) {
        return <HStack w={"100%"} marginTop={"10px"}>

            <Box w={"50%"}>
                <Text textAlign={"center"} cursor={"pointer"} borderRadius={"10px"}
                      _hover={{
                          background: "yellow.200",
                      }}
                      onClick={() => api.addLike(localStorage.getItem("authToken"), post.map.post.ID)
                          .then(() => this.refreshFeed())
                      }
                >
                    👍 Like
                </Text>
            </Box>

            <Box w={"50%"}>
                <Text textAlign={"center"} cursor={"pointer"} borderRadius={"10px"}
                      _hover={{background: "yellow.200"}}
                      onClick={() => {
                          if (this.state.openComments === k) {
                              this.setState({openComments: -1})
                          } else {
                              this.setState({openComments: k});
                          }

                      }}
                >
                    💬 Open comments
                </Text>
            </Box>

        </HStack>;
    }

    private renderContentField(k: number, post: PostJson) {
        return <>
            {this.state.openComments === k ?
                <>
                    {this.state.commentingOnPost ?
                        <Box borderWidth="1px" marginTop="10px" borderColor={"white"}>

                            <Textarea
                                marginTop="5px"
                                placeholder={"type comment here"}
                                onChange={(e) => this.setState({comment: e.target.value})}

                            />
                            <Box h="20px"> </Box>

                            <Box w="100%">

                                <Button float="right" colorScheme="red" marginLeft="15px"
                                        onClick={() => this.setState({commentingOnPost: false})}>
                                    Cancel
                                </Button>

                                <Button float="right" colorScheme="green"
                                        onClick={() => {
                                            api.makeComment(
                                                localStorage.getItem("authToken"),
                                                post.map.post.ID,
                                                this.state.comment,
                                                null,
                                            ).then(() => this.refreshFeed());
                                        }}>
                                    Comment
                                </Button>

                            </Box>

                        </Box> :
                        <Center>
                            <Button marginTop="10px"
                                    onClick={() => this.setState({commentingOnPost: true})}>
                                Comment on post
                            </Button>
                        </Center>
                    }
                    <Box height="40px"> </Box>

                    {/* Render all comments here */}
                    {post.map.comments.map((cp, cpIndex) => {
                        return this.renderComment(cp, cpIndex)
                    })}
                </> : <> </>
            }
        </>;
    }

    private renderPostContent(post: PostJson) {
        return <Box>
            <Center>
                <Carousel dynamicHeight={true}>
                    {post.map.images?.map(function (url: string, i) {
                        return (
                            <div>
                                <img src={url}/>
                            </div>
                        )
                    })}
                </Carousel>
            </Center>

            <Text fontSize={"20px"}> {post.map.post.contents}  </Text>

            <HStack w={"100%"} marginTop={"10px"}>
                <Box>
                    <Text color={"orange.400"}> Likes : {post.map.likeCount} </Text>
                </Box>
            </HStack>
        </Box>;
    }

    private renderComment(cp: CommentJson, cpIndex: number) {
        return (
            <>

                <Box>
                    <HStack color={"black"}>
                        <Avatar name={cp.map.username} bg="teal.400" src={cp.map.avatar} w="50px"/>
                        <Text fontWeight={500} marginLeft={"10px"}> {cp.map.username} </Text>
                    </HStack>
                </Box>

                <HStack>

                    <Box marginLeft="75px" w="75%">
                        <Text> {cp.map.contents} </Text>
                    </Box>

                    {this.state.commentingNested === cpIndex ?
                        <>
                            <Button onClick={() => {
                                api.makeComment(
                                    localStorage.getItem("authToken"),
                                    cp.map.postID,
                                    this.state.comment,
                                    cp.map.ID)
                                    .then(() => this.refreshFeed());
                            }}>
                                ✔
                            </Button>
                            <Button onClick={() => this.setState({commentingNested: -1})}>
                                ❌
                            </Button>
                        </> :
                        <Button onClick={() => this.setState({commentingNested: cpIndex})}>
                            💬
                        </Button>
                    }


                </HStack>

                {this.state.commentingNested === cpIndex ?
                    <Textarea marginLeft="75px" w="75%" placeholder={"Type comment here..."}
                              onChange={(e) =>
                                  this.setState({comment: e.target.value})}
                    >
                    </Textarea>
                    : <> </>
                }



                {cp.map.children.map((cc, ccIndex) => {
                    {
                        return this.renderNestedComment(cc)
                    }
                })}

            </>
        );
    }

    private renderNestedComment( comment:CommentJson) {
        return (
            <>
                <Box paddingLeft={"30px"}>
                    <HStack color={"black"}>
                        <Avatar name={comment.map.username} bg="teal.400" src={comment.map.avatar} w="50px"/>
                        <Text fontWeight={500} marginLeft={"10px"}> { comment.map.username } </Text>
                    </HStack>
                </Box>

                <Box marginLeft="100px" w="75%" >
                    <Text> {comment.map.contents} </Text>
                </Box>
            </>
        )
    }
}

export default UserFeed;