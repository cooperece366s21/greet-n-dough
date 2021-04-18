import React from 'react';

import {
    Box,
    Center,
    Text,
    Textarea,
    Input,
    Button,

} from "@chakra-ui/react";

import api, {register} from "../../services/api";

type PostState = {
    title: string;
    contents: string;
    invalid: boolean;
}

class PostForm extends React.Component<any, any> {

    state: PostState = {
        title: "",
        contents: "",
        invalid: false,
    }

    async createPostWrapper( title:string, contents:string ) {
        let token = localStorage.getItem("authToken");

        let res = await api.createPost(token, title, contents);

        if ( res === 200 ) {
            alert("Post successfully made!");
        }
        else {
            alert("SOMETHING WENT VERY WRONG");
        }
    }

    render() {

        return (
            <>
                <Center>

                    <Box w="50%"  borderWidth={"0px"}>

                        <Center>
                            <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"} > Title </Text>
                        </Center>

                        <Input placeholder={'Type your title here...'}
                               onChange={ e => this.setState( {title: e.target.value }) }
                               isInvalid={this.state.invalid}
                               size={'lg'}
                               errorBorderCOlor={'tomato'}
                               marginBottom={"40px"}
                        />

                        <Center>
                            <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"} > Contents </Text>
                        </Center>

                        <Textarea placeholder={'Type your post content here...'}
                                  height={"300px"}
                                  onChange={ e => this.setState( {contents: e.target.value }) }
                                  isInvalid={this.state.invalid}
                                  size={'lg'}
                                  errorBorderCOlor={'tomato'} />

                        <Box float={'right'}>

                          <Button colorScheme={"green"}
                                  onClick={ () => this.createPostWrapper( this.state.title, this.state.contents)}>
                            submit
                          </Button>

                        </Box>

                    </Box>

                </Center>

            </>
        )
    }
}


export default PostForm;