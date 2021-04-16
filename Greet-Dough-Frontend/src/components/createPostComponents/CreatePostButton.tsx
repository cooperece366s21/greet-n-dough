import React from 'react';
import {
    Button,
    Link,
} from "@chakra-ui/react";

type OwnershipState = {
    hasOwnership: boolean | null
}

class CreatePostButton extends React.Component<any, any> {

    state: OwnershipState = {
        hasOwnership: false
    }

    constructor(props:any) {
        super(props);
        this.state = {
            hasOwnership: props.hasOwnership
        }
    }

    redirectToCreatePost(){
        window.location.replace("/create");
    }


    renderEmpty() {
        return( <> </> );
    }

    renderButton() {
        return(

            <Link to="/create/">
                <Button colorScheme={"yellow"} onClick={this.redirectToCreatePost}>
                    Create Post
                </Button>
            </Link>
        )

    }

    render() {
        switch(this.state.hasOwnership) {

            case true:
                return this.renderButton();

            default:
                return this.renderEmpty();

        }
    }
}

export default CreatePostButton;