import React from 'react'
import RegisterForm from "../components/RegisterForm";
import {Center, Text} from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import HeaderChakra from "../components/HeaderChakra";
import UserHeader from "../components/userpageComponents/UserHeader";
import api from "../services/api";

function User() {

    const { uid } = useParams<{ uid: string }>();

    return(
        <>
            < HeaderChakra />
            <UserHeader uid={uid}/>
        </>

    )
}

export default User;