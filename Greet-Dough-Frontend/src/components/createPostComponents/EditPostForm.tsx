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
import {PostObject, UploadedImage} from "../../services/types"
import {toast} from "react-toastify";

type PostState = {
    title: string;
    contents: string;
    invalid: boolean;
    pid: string;
    pictures: File[] | null;
    tier: null;
    loadedImages: UploadedImage[] | null;
    deleted : number[];

}

class EditPostForm extends PostForm{

    state: PostState = {
        title: "",
        contents: "",
        invalid: false,
        pid: "",
        pictures: null,
        tier: null,
        loadedImages: null,
        deleted: [],
    }

    constructor(props:any) {
        super(props);
        this.state = {
            title: "",
            contents: "",
            invalid: false,
            pid: props.pid,
            pictures: null,
            tier: null,
            loadedImages: null,
            deleted: [],
        }
    }

    componentDidMount() {
        api.getPost( localStorage.getItem("authToken"), parseInt(this.state.pid) )
            .then( body => {
                var images:UploadedImage[] = [];

                body.urls.forEach( (url:string, k:number) => {
                    let image:UploadedImage = {
                        url: url,
                        id: body.imageIDList.myArrayList[k],
                    }
                    images.push( image );
                } )

                this.setState({
                    title: body.title,
                    contents: body.contents,
                    tier: body.tier,
                    loadedImages: images,
                })
            })
    }

    renderSubmitButton(){
        const editGood = () => toast.success("Post edited!",
            { style:{ backgroundColor:"#5da356"} }
        );
        const editBad = () => toast.error("Could not edit the post!");
        const selectATier = () => toast.error("Please select a tier!");
        const writeATitle = () => toast.error("Please write a title!");

        return(
            <Button colorScheme={"green"}
                    onClick={ () => {

                        if ( this.state.title==="" ) { writeATitle(); return; }
                        if ( this.state.tier==null ) { selectATier(); return; }

                        api.editPost(
                            this.state.pid,
                            this.state.title,
                            this.state.contents,
                            this.state.pictures,
                            this.state.tier,
                            this.state.deleted,
                        ).then( res => {
                            if(res===200){
                                editGood();
                            } else{
                                editBad();
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