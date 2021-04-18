import React from 'react'
import RegisterForm from "../components/authComponents/RegisterForm";
import {Center, Text} from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import Header from "../components/globalComponents/Header";
import UserPageWrapper from "../components/userpageComponents/UserPageWrapper";
import UserHeader from "../components/userpageComponents/UserHeader";
import api from "../services/api";

function User() {

    const { uid } = useParams<{ uid: string }>();

    return(
        <>
            < Header />
            <UserPageWrapper uid={uid} />
        </>

    )
}

export default User;