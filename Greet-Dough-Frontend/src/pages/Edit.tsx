import React from 'react'
import Header from "../components/globalComponents/Header";
import EditPostForm from "../components/createPostComponents/EditPostForm";
import {useParams} from "react-router-dom";

function Edit(){

    const { pid } = useParams<{ pid: string }>();

    return (

        <>
            <Header/>
        <EditPostForm pid={pid}/>
        </>

    );
}

export default Edit;