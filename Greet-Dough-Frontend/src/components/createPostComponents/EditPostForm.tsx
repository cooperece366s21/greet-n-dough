import React from 'react';

import {
    Box,
    Center,
    Text,
    Textarea,
    Input,
    Button,

} from "@chakra-ui/react";

import api, {register} from "../../services/api";
import PostForm from "./PostForm";
import {PostObject} from "../../services/types"

type PostState = {
    title: string;
    contents: string;
    invalid: boolean;
    pid: string;
}

class EditPostForm extends PostForm{

    state: PostState = {
        title: "",
        contents: "",
        invalid: false,
        pid: "",
    }

    constructor(props:any) {
        super(props);
        this.state = {
            title: "",
            contents: "",
            invalid: false,
            pid: props.pid,
        }
    }

    componentDidMount() {
        api.getPost( localStorage.getItem("authToken"), parseInt(this.state.pid) )
            .then( body => {
                this.setState({
                    title: body.post.title,
                    contents: body.post.contents,
                })
            })
    }

    renderSubmitButton(){
        return(
            <Button colorScheme={"green"}
                    onClick={ () => {
                        api.editPost(
                            localStorage.getItem("authToken"),
                            this.state.pid,
                            this.state.title,
                            this.state.contents
                        ).then( res => {
                            if(res===200){
                                alert("Edit successful!");
                            }
                        })
                    }}>
                Edit post
            </Button>
        )
    }
    render(){
        return super.render();
    }
}

export default EditPostForm