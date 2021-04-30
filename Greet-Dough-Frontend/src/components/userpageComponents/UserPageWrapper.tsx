import React from "react";
import UserFeed from "./UserFeed";
import UserHeader from "./UserHeader";
import api from "../../services/api";
import {Center, Text} from "@chakra-ui/react";

// The purpose of this class is to have a class component that does basic API calls (Get user ID, other user things)
// and then feeds down the information down to more basic rendering components

type PassdownStates = {
    uid : number,
    cuid: number | null,
    name : string | null,
    exists : boolean | null,  // Null state for rendering blank when not yet explicitly set to boolean
    hasOwnership : boolean | null,
    bio : string | null,
    profilePicture: string | null,
}

class UserPageWrapper extends React.Component<any, any> {

    state: PassdownStates = {
        cuid: -1,
        uid: -1,
        name: "",
        exists: null,
        hasOwnership: null,
        bio: null,
        profilePicture: null,
    }

    constructor(props:any) {
        super(props);
        this.state = {
            cuid: null,
            uid : props.uid,
            name : null,
            exists: null,
            hasOwnership: null,
            bio: null,
            profilePicture: null,
        }
    }

    componentDidMount() {

        api.getUserProfile( this.state.uid )
            .then( r => {

                if ( r.map.bio !== null ) {
                    this.setState({bio: r.map.bio});
                }

                if ( r.map.profilePicture !== null ) {
                    this.setState({profilePicture: r.map.profilePicture})
                }

            } );

        api.getUserID()
            .then( cuid => {
                cuid === parseInt(String(this.state.uid)) ?
                    this.setState({hasOwnership:true, cuid:cuid}) :
                    this.setState({hasOwnership:false, cuid:cuid});

                api.getUser(this.state.uid)
                    .then( user => {

                        if( user === 404 ) {
                            this.setState( {exists: false} );
                            return;
                        }

                        this.setState( {
                            name: user.name,
                            exists: true,
                        });

                        api.getAllUserImages(localStorage.getItem("authToken"), this.state.uid);
                    })
            })
    }

    renderError() {
        return(
            <Center>
                <Text fontSize="4xl" color="red.500"> User does not exist! </Text>
            </Center>
        )
    }

    renderUser(){

        return (
            <>
                <UserHeader
                    uid={this.state.uid}
                    name={this.state.name}
                    hasOwnership={this.state.hasOwnership}
                    exists={this.state.exists}
                    bio={this.state.bio}
                    profilePicture={this.state.profilePicture}
                />

                <UserFeed
                    uid={this.state.uid}
                    cuid={this.state.cuid}
                    hasOwnership={this.state.hasOwnership}
                />

            </>
        )
    }

    render() {

        switch( this.state.exists ) {

            case true:
                return this.renderUser();

            case false:
                return this.renderError();

            default:
                return ( <> </> );
        }
    }
}

export default UserPageWrapper;