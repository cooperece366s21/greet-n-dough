import React from 'react';
import {
    Box,
    VStack,
    HStack,
    Avatar,
    Center,
    Input,
    Image,
    Button,
    Flex, Text, SkeletonCircle,
} from "@chakra-ui/react";
import {Link} from "react-router-dom";
import api from "../../services/api";
import CreatePostButton from "../createPostComponents/CreatePostButton";
import ImageUploader from "react-images-upload";

import { withRouter } from 'react-router-dom';


type UserState = {
    editing: boolean;
    exists: boolean | null;
    hasOwnership : boolean;

    uid: number;
    name: string;
    bio: string | null;
    profilePicture: string | null;

    editedName : string;
    editedBio : string | null;
    uploadedPicture : File | null;
}

class UserHeader extends React.Component<any, any> {

    state: UserState = {
        exists: null,
        hasOwnership: false,
        editing: false,

        uid: -1,
        name: "",
        bio: null,
        profilePicture: null,

        editedName: "",
        editedBio: null,
        uploadedPicture: null,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            exists: props.exists,
            hasOwnership: props.hasOwnership,
            editing: false,

            uid: props.uid,
            name: props.name,
            bio: props.bio,
            profilePicture: props.profilePicture,
            editedName: "",
            editedBio: props.bio,
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

    renderHeader() {

        let nameAndEditButtons = <HStack>


            { this.state.editing ?
                <Input
                    placeholder={this.state.editedName}  size="lg"
                    onChange={ e => this.setState( {editedName: e.target.value }) }
                    value={this.state.editedName}
                />
                : <Text fontSize={'4xl'} fontWeight={500} w="80%"> {this.state.name} </Text>
            }

            { this.state.hasOwnership ?
                <> </> :
                <Button colorScheme={"green"}
                        onClick={ () => alert("Follow api goes here")}
                >
                    Follow
                </Button>
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
                        let token = localStorage.getItem("authToken");

                        api.editUser(token,  this.state.editedName,  this.state.editedBio )
                            .then( res => {
                                if(res===200){
                                    // As to not need to refresh to see changes
                                    this.setState({
                                        name: this.state.editedName,
                                        bio: this.state.editedBio,
                                    })
                                }
                            })

                        api.uploadProfilePicture( token, this.state.uploadedPicture );
                        this.setState({editing: false});

                    }}>
                        ✔
                    </Button>

                    <Button onClick={() => this.setState({editing: false})}>
                        ❌
                    </Button>

                </>
                : <> </>
            }

        </HStack>;
        let profilePicture = <>

            {this.state.editing ?
            <ImageUploader
                withIcon={false}
                withPreview={true}
                buttonText="Choose image"
                onChange={e => {

                    if (e.length !== 1) {
                        alert("Remove some files");
                        return;
                    }
                    this.onDrop(e[0]);
                }
                }
                imgExtension={[".jpg", ".gif", ".png", ".gif"]}
                maxFileSize={5242880}
            /> :
            this.state.profilePicture ?
                <Avatar src={this.state.profilePicture} bg="teal.500" size={"2xl"} /> :
                <> </>
        }</>;
        let biography = <>{this.state.editing ?
            <Input
                placeholder={this.state.bio ? this.state.bio : ""} size="lg"
                onChange={e => this.setState({editedBio: e.target.value})}
                value={this.state.editedBio ? this.state.editedBio : ""}
            /> :
            <Text> {this.state.bio ? this.state.bio : "No biography"} </Text>
        }</>;

        return(

             <>
                 <Center marginTop="40px">

                     {/* PROFILE PICTURE*/}
                     <Box w="175px" height="175px" borderWidth="0px">
                         {profilePicture}
                     </Box>

                     {/* RHS TEXT CONTAINER*/}
                     <Box w="45%" height="175px" marginLeft={"20px"} borderWidth="0px">

                         {/*UPPER TEXT CONTAINER*/}
                         <Box w="100%" h="80px">
                             {nameAndEditButtons}
                         </Box>

                         {/*LOWER TEXT CONTAINER*/}
                         <Box w="80%" h="95px" paddingTop="30px">
                             {biography}
                         </Box>

                     </Box>

                 </Center>

                 <Center>
                     <Box marginTop="30px" marginLeft="40%">
                         <CreatePostButton hasOwnership={this.state.hasOwnership}/>
                     </Box>
                 </Center>

             </>

         )
     }


    render() {
        switch( this.state.exists ) {

            case true:
                return this.renderHeader();

            default:
                return ( <> </> );
        }
    }

}

export default UserHeader;