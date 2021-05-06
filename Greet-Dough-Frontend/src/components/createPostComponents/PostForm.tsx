import React from 'react';

import {
    Box,
    Center,
    Text,
    Textarea,
    Input,
    Button,
    Image,
    Stack,
    RadioGroup,
    Radio,
} from "@chakra-ui/react";

import api, {register} from "../../services/api";
import ImageUploader from "react-images-upload";

type PostState = {
    title: string;
    contents: string;
    invalid: boolean;
    pictures: File[] | null;
    tier: string | null,
}

class PostForm extends React.Component<any, any> {

    state: PostState = {
        title: "",
        contents: "",
        invalid: false,
        pictures: null,
        tier: null,
    }

    onDrop(pictures:File[]) {
        this.setState({ pictures: pictures });
    }

    renderSubmitButton(){
        return(
            <Button colorScheme={"green"}
                    onClick={ () => {
                        let token = localStorage.getItem('authToken');

                        api.createPost( token, this.state.title, this.state.contents, this.state.pictures, this.state.tier )
                            .then( res => {
                                if (res===200) alert("Post succesful!");
                                else alert("ERROR: " + res);
                            })
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
                            <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"} > Pictures </Text>
                        </Center>

                        <ImageUploader
                            withIcon={false}
                            withPreview={true}
                            buttonText="Choose images"
                            onChange={ e => this.onDrop(e) }
                            imgExtension={[".jpg", ".gif", ".png", ".gif"]}
                            maxFileSize={10485760}
                        />

                        <Center>
                            <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"} > Select Tier </Text>
                        </Center>

                        <Center>
                            <RadioGroup onChange={ (e) => {
                                this.setState({tier: e});
                            }}>
                                <Stack spacing={4} direction="row" >
                                    <Radio value="1" colorScheme={"green"}> 1 </Radio>
                                    <Radio value="2" colorScheme={"green"}> 2 </Radio>
                                    <Radio value="3" colorScheme={"green"}> 3 </Radio>
                                    <Radio value="4" colorScheme={"green"}> 4 </Radio>
                                    <Radio value="5" colorScheme={"green"}> 5 </Radio>
                                </Stack>
                        </RadioGroup>
                        </Center>

                        <Center>
                            <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"} paddingTop={"30px"} >
                                Contents
                            </Text>
                        </Center>

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