import React from 'react';

import {
    Box,
    Center,
    Text,
    HStack,
    Textarea,
    Input,
    Button,
    Image,
    CheckboxGroup,
    Stack,
    RadioGroup, Radio,
} from "@chakra-ui/react";

import api, {register} from "../../services/api";
import {UploadedImage} from "../../services/types";
import ImageUploader from "react-images-upload";
import {css, style} from "glamor";
import {toast, ToastContainer} from "react-toastify";

type PostState = {
    title: string;
    contents: string;
    invalid: boolean;
    pictures: File[] | null;
    tier: number | null;
    loadedImages: UploadedImage[] | null;
    deleted : number[];
}

class PostForm extends React.Component<any, any> {

    state: PostState = {
        title: "",
        contents: "",
        invalid: false,
        pictures: null,
        tier: null,
        loadedImages: null,
        deleted: [],
    }

    onDrop(pictures:File[]) {
        this.setState({ pictures: pictures });
    }

    renderSubmitButton(){
        const goodPost = () => toast.success("Post created!",
            { style:{ backgroundColor:"#5da356"} }
            );

        const selectATier = () => toast.error("Please select a tier!");
        const writeATitle = () => toast.error("Please write a title!");
        const otherErrors = (res:number) => toast.error("Post could not be created! " +  res );

        return(
            <Button colorScheme={"green"}
                    onClick={ () => {
                        let token = localStorage.getItem('authToken');

                        if ( this.state.title==="" ) { writeATitle(); return; }
                        if ( this.state.tier==null ) { selectATier(); return; }

                        api.createPost( token, this.state.title, this.state.contents, this.state.pictures, this.state.tier )
                            .then( res => {
                                if (res===200) goodPost();
                                else otherErrors(res);
                            })
                    }}>
                submit
            </Button>
        )
    }

    private renderContentInput() {
        return <>
            <Center>
                <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"} paddingTop={"30px"}>
                    Contents
                </Text>
            </Center>

            <Textarea placeholder={'Type your post content here...'}
                      height={"300px"}
                      onChange={e => this.setState({contents: e.target.value})}
                      isInvalid={this.state.invalid}
                      size={'lg'}
                      errorBorderCOlor={'tomato'}
                      value={this.state.contents}
            />
        </>;
    }

    private renderTierInput() {
        return <>
            <Center>
                <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"}> Select Tier </Text>
            </Center>

            <Center>
                <RadioGroup onChange={(e) => {
                    this.setState({tier: e});
                }}>
                    <CheckboxGroup>
                        <Stack spacing={4} direction="row">
                            {this.state.tier === 1 ?
                                <Radio value="1" isInvalid colorScheme={"red"}> 1 </Radio> :
                                <Radio value="1" colorScheme={"red"}> 1 </Radio>
                            }
                            {this.state.tier === 2 ?
                                <Radio value="2" isInvalid colorScheme={"red"}> 2 </Radio> :
                                <Radio value="2" colorScheme={"red"}> 2 </Radio>
                            }
                            {this.state.tier === 3 ?
                                <Radio value="3" isInvalid colorScheme={"red"}> 3 </Radio> :
                                <Radio value="3" colorScheme={"red"}> 3 </Radio>
                            }
                            {this.state.tier === 4 ?
                                <Radio value="4" isInvalid colorScheme={"red"}> 4 </Radio> :
                                <Radio value="4" colorScheme={"red"}> 4 </Radio>
                            }
                            {this.state.tier === 5 ?
                                <Radio value="5" isInvalid colorScheme={"red"}> 5 </Radio> :
                                <Radio value="5" colorScheme={"red"}> 5 </Radio>
                            }
                        </Stack>
                    </CheckboxGroup>
                </RadioGroup>
            </Center>
        </>;
    }

    private renderImageInput() {
        return <>
            <Center>
                <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"}> Pictures </Text>
            </Center>

            <ImageUploader
                withIcon={false}
                withPreview={true}
                buttonText="Choose images"
                onChange={e => this.onDrop(e)}
                imgExtension={[".jpg", ".gif", ".png", ".gif"]}
                maxFileSize={10485760}
            />
        </>;
    }

    private renderTitleInput() {
        return <>
            <Center>
                <Text fontSize={"3xl"} fontWeight={"700"} color={"gray.500"}> Title </Text>
            </Center>

            <Input placeholder={'Type your title here...'}
                   onChange={e => this.setState({title: e.target.value})}
                   isInvalid={this.state.invalid}
                   size={'lg'}
                   errorBorderCOlor={'tomato'}
                   marginBottom={"40px"}
                   value={this.state.title}
            />
        </>;
    }

    render() {

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

                <Center>


                    <Box w="50%" borderWidth={"0px"}>

                        {this.renderTitleInput()}

                        {this.renderImageInput()}

                        {this.renderLoadedImages()}

                        {this.renderTierInput()}

                        {this.renderContentInput()}

                        <Box float={'right'}>
                            {this.renderSubmitButton()}
                        </Box>

                    </Box>

                </Center>

            </>
        )
    }


    private renderLoadedImages() {
        return <HStack>
            {this.state.loadedImages && this.state.loadedImages.map((img: UploadedImage, k: number) => (
                <>
                    <Box cursor={"pointer"}
                         onClick={() => {
                             let index = this.state.deleted.findIndex((v) => v == img.id);

                             if (index === -1) {
                                 let newList = this.state.deleted.concat(img.id);
                                 this.setState({deleted: newList});
                             } else {
                                 // holy shit i hate javascript this is the only way I can get
                                 // an array element deleted by an index
                                 let newList = this.state.deleted.slice(0, index)
                                     .concat(this.state.deleted.slice(index + 1));
                                 this.setState({deleted: newList});
                             }
                         }}>

                        {this.state.deleted.includes(img.id) ?
                            <Image w="100px" src={img.url} filter={"grayscale(100%) blur(3px)"}/> :
                            <Image w="100px" src={img.url}/>
                        }

                    </Box>
                </>
            ))}
        </HStack>;
    }
}


export default PostForm;