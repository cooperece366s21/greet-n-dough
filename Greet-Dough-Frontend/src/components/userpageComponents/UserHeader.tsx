import React from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {
    Box,
    VStack,
    HStack,
    Avatar,
    Select,
    Center,
    Input,
    Image,
    Button,
    Stat, StatLabel, StatNumber, StatHelpText, StatArrow, StatGroup,
    Flex, Text, SkeletonCircle,
} from "@chakra-ui/react";
import {Link} from "react-router-dom";
import api, {uploadProfilePicture} from "../../services/api";
import CreatePostButton from "../createPostComponents/CreatePostButton";
import ImageUploader from "react-images-upload";

import { withRouter } from 'react-router-dom';


type UserState = {
    editing: boolean;
    exists: boolean | null;
    hasOwnership : boolean;
    tierText : string;

    uid: number;
    name: string;
    bio: string | null;
    profilePicture: string | null;
    subscribers: number,

    editedName : string;
    editedBio : string | null;
    uploadedPicture : File | null;
}

class UserHeader extends React.Component<any, any> {

    state: UserState = {
        exists: null,
        hasOwnership: false,
        editing: false,
        tierText : "Subscribe",

        uid: -1,
        name: "",
        bio: null,
        profilePicture: null,
        subscribers: 0,

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
            tierText: "Subscribe",

            uid: props.uid,
            name: props.name,
            bio: props.bio,
            profilePicture: props.profilePicture,
            subscribers: props.subscribers,

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
        const editGood = () => toast.success("Edit was successful!",
            { style:{ backgroundColor:"#5da356"} }
        );

        const editBad = () => toast.error(" Edit could not be made! ");

        const pictureUploadGood = () => toast.success("Profile picture was successfully uploaded!",
            { style:{ backgroundColor:"#5da356"} }
        );

        const pictureUploadBad = () => toast.error( "Picture could not be uploaded! ");

        const noMoney = () => toast.error("Insufficient funds!");

        const tooManyImages = () => toast.error("Please remove the extra images!");

        const badResponse = (res:number) => toast.error("Error: " + res);
        let upperHeader = <HStack>


            { this.state.editing ?
                <Input
                    placeholder={this.state.editedName}  size="lg"
                    onChange={ e => this.setState( {editedName: e.target.value }) }
                    value={this.state.editedName}
                />
                : <Text fontSize={'4xl'} fontWeight={500} w="80%"> {this.state.name} </Text>
            }

                <Stat paddingRight={"20px"}>
                    <StatLabel> Subscribers </StatLabel>
                    <Center> <StatNumber> {this.state.subscribers} </StatNumber> </Center>
                </Stat>


            { this.state.hasOwnership ?
                <> </> :
                <Select
                    w={"25%"}
                    bg={"green.300"}
                    color={"black"}
                    placeholder={this.state.tierText}
                    onChange={ (tier) => {
                        api.subscribeTo( this.state.uid, parseInt(tier.target.value) )
                            .then( (res) => {
                                if (res===200) {
                                    window.location.href = ("/user/" + this.state.uid)
                                }
                                else if ( res === 402 ) {
                                    return noMoney();
                                }
                                else {
                                    return badResponse(res);
                                }
                            });
                    } }
                >
                    <option value={1}>Tier 1 $5</option>
                    <option value={2}>Tier 2 $10</option>
                    <option value={3}>Tier 3 $15</option>
                    <option value={4}>Tier 4 $20</option>
                    <option value={5}>Tier 5 $25</option>
                </Select>
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
                                if (res===200) {
                                    editGood();
                                    this.setState({
                                        name: this.state.editedName,
                                        bio: this.state.editedBio,
                                    });
                                } else {
                                    editBad();
                                }
                            })

                        if ( this.state.uploadedPicture != null) {
                            api.uploadProfilePicture(token, this.state.uploadedPicture)
                                .then((res) => {
                                    this.setState({editing: false});
                                    if (res == 200) {
                                        pictureUploadGood();
                                    } else {
                                        pictureUploadBad();
                                    }
                                })
                        }

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
                        tooManyImages();
                        return;
                    }
                    this.onDrop(e[0]);
                }
                }
                imgExtension={[".jpg", ".gif", ".png", ".gif"]}
                maxFileSize={5242880}
            /> :
            this.state.profilePicture ?
                <Avatar src={this.state.profilePicture} name={this.state.name} bg="teal.400" size={"2xl"} /> :
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
                             {upperHeader}
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
                        {this.renderHeader()}
                    </>
                )
            ;

            default:
                return ( <> </> );
        }
    }

}

export default UserHeader;