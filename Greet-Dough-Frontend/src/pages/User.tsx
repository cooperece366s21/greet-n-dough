import React from 'react'
import RegisterForm from "../components/RegisterForm";
import {Center, Text} from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import HeaderChakra from "../components/HeaderChakra";
import UserHeader from "../components/UserHeader";

function User() {

    const { uid } = useParams<{ uid: string }>();
    // Check existence here

    return(
        <>
            < HeaderChakra />
            <UserHeader uid={uid}/>
        </>

    )
}

export default User;