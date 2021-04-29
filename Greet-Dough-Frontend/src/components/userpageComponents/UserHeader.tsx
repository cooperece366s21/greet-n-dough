import React from 'react';
import {
    Box,
    VStack,
    HStack,
    Center,
    Input,
    Button,
    Flex, Text, SkeletonCircle,
} from "@chakra-ui/react";
import {Link} from "react-router-dom";
import api from "../../services/api";
import CreatePostButton from "../createPostComponents/CreatePostButton";
import ImageUploader from "react-images-upload";

import { withRouter } from 'react-router-dom';


type UserState = {
    uid: number;
    name: string;
    followers: number | null;
    posts: number | null;
    exists: boolean | null;
    hasOwnership : boolean | null;

    editing: boolean;
    editedName : string;
    uploadedPicture : File | null;
}

class UserHeader extends React.Component<any, any> {

    state: UserState = {
        uid: -1,
        name: "",
        followers:  null,
        posts: null,
        exists: null,
        hasOwnership: null,

        editing: false,
        editedName: "",
        uploadedPicture: null,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            uid: props.uid,
            name: props.name,
            followers: null,
            posts:null,
            exists: props.exists,
            hasOwnership: props.hasOwnership,

            editing: false,
            editedName: "",
            uploadedPicture: null,
        }
    }

    onDrop(picture:File) {
        this.setState({ uploadedPicture: picture });
    }

    renderError() {
        return(
            <Center>
                <Text fontSize="4xl" color="red.500"> User does not exist! </Text>
            </Center>
        )
    }

    renderUser() {

         return(

             <>
                <Center marginTop="40px">

                     <Box w="175px" height="175px" borderWidth="0px" >
                         {this.state.editing ?
                             <ImageUploader
                                 withIcon={false}
                                 withPreview={true}
                                 buttonText="Choose image"
                                 onChange={ e => {

                                     if (e.length!==1) {
                                         alert("Remove some files");
                                         return;
                                     }
                                     this.onDrop(e[0]);
                                 }
                                 }
                                 imgExtension={[".jpg", ".gif", ".png", ".gif"]}
                                 maxFileSize={5242880}
                             /> :
                             <SkeletonCircle size="175px"/>
                         }
                     </Box>

                    {/* RHS TEXT CONTAINER*/}
                    <Box w="45%" height="175px" marginLeft={"20px"} borderWidth="0px">

                        {/*UPPER TEXT CONTAINER*/}
                        <Box w="100%" h="80px" >
                            <HStack>


                                { this.state.editing ?
                                    <Input
                                        placeholder={this.state.editedName}  size="lg"
                                        onChange={ e => this.setState( {editedName: e.target.value }) }
                                        value={this.state.editedName}
                                    />
                                    : <Text fontSize={'4xl'} fontWeight={500} w="80%"> {this.state.name} </Text>
                                }

                                { this.state.hasOwnership && !this.state.editing ?
                                    <Button onClick={() => this.setState({
                                        editing: true,
                                        editedName: this.state.name,
                                    })}>
                                        ✏
                                    </Button>
                                    : <> </>
                                }

                                { this.state.editing ?
                                    <>

                                    <Button onClick={() => {
                                        this.setState({editing: false});
                                        api.editUser( localStorage.getItem("authToken"), this.state.editedName)
                                            .then( res => {
                                                if(res===200){
                                                    this.setState({name: this.state.editedName})
                                                }
                                            })
                                        api.postImage( localStorage.getItem("authToken"), this.state.uploadedPicture)
                                            .then( () => alert("yo poggies") )
                                    }}>
                                        ✔
                                    </Button>

                                    <Button onClick={() => this.setState({editing: false})}>
                                        ❌
                                    </Button>

                                    </>
                                    : <> </>
                                }

                            </HStack>
                        </Box>

                        {/*LOWER TEXT CONTAINER*/}
                        <Box w="80%" h="95px"  paddingTop="30px">
                            <Text> Lorem ipsum dolor sit amet, consectetur adipisicing elit. Doloribus eligendi minus nam sequi ullam. A ab alias commodi fugiat magni nobis non, perspiciatis quibusdam quis quos, ullam ut voluptas voluptatum?</Text>
                        </Box>

                    </Box>

                </Center>

                 <Center>
                     <Box marginTop="30px" marginLeft="40%">
                         <CreatePostButton hasOwnership={this.state.hasOwnership} />
                     </Box>
                 </Center>

             </>

         )
     }


    render() {
        switch( this.state.exists ) {

            case true:
                return this.renderUser();

            default:
                return ( <> </> );
        }
    }

}

export default UserHeader;