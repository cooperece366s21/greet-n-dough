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
import ImageUploader from "react-images-upload";

type PostState = {
    title: string;
    contents: string;
    invalid: boolean;
    pictures: File[] | null;
}

class PostForm extends React.Component<any, any> {

    state: PostState = {
        title: "",
        contents: "",
        invalid: false,
        pictures: null,
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

    onDrop(pictures:File[]) {
        this.setState({ pictures: pictures });
    }

    renderSubmitButton(){
        return(
            <Button colorScheme={"green"}
                    onClick={ () => {
                        let token = localStorage.getItem('authToken');

                        api.createPost( token, this.state.title, this.state.contents )
                            .then( res => {
                                if (res===200) alert("Post succesful!");
                                else alert("ERROR: " + res);
                            })

                        this.state.pictures?.forEach( pic => {
                            api.postImage( token, pic )
                        })
                        // this.createPostWrapper( this.state.title, this.state.contents)
                    }}>
                submit
            </Button>
        )
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
                               value={this.state.title}
                        />

                        <Center>
                            <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"} > Contents </Text>
                        </Center>

                        <ImageUploader
                            withIcon={false}
                            withPreview={true}
                            buttonText="Choose images"
                            onChange={ e => this.onDrop(e) }
                            imgExtension={[".jpg", ".gif", ".png", ".gif"]}
                            maxFileSize={5242880}
                        />

                        <Textarea placeholder={'Type your post content here...'}
                                  height={"300px"}
                                  onChange={ e => this.setState( {contents: e.target.value }) }
                                  isInvalid={this.state.invalid}
                                  size={'lg'}
                                  errorBorderCOlor={'tomato'}
                                  value={this.state.contents}
                        />

                        <Box float={'right'}>
                            {this.renderSubmitButton()}
                        </Box>

                    </Box>

                </Center>

            </>
        )
    }
}


export default PostForm;