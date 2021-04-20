export const BACKEND_URL = "http://localhost:5432";

function getCurrentToken(): string {
    return localStorage.getItem("authToken") || "";
}

function setCurrentToken( token:string): void {
    localStorage.setItem("authToken", token);
}

export function logout() {
    localStorage.removeItem("authToken");
    window.location.replace("/");
}

export async function getUserID(): Promise<number> {
    let authToken = getCurrentToken();

    if (authToken === "") {
        return -1;
    }

    const res = await fetch(`${BACKEND_URL}/auth/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ authToken })
    });

    if ( res.ok ) {

        let resJSON = await res.json()
            .then( body => {
                return ( JSON.parse(body).uid );
            })

        return resJSON;

    }

    else {
        // alert( "Invalid Loggin Session" );
        return -1;
    }
}

export async function register(  email:string, username:string, password:string ) {

    const res = await fetch(`${BACKEND_URL}/users/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, username, password })
    });

    if ( res.ok ) {
        return 200;
    } else {
        // maybe some other code here for specific errors?
        return res.status;
    }

}

export async function login( email:string, password:string ) {

    // alert( JSON.stringify({ email, password } ) )

    const res = await fetch(`${BACKEND_URL}/login/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    if ( res.ok ) {

        return await res.json()
            .then(body => {
                setCurrentToken(JSON.parse(body).authToken);
                return 200;
            });

    } else {
        // maybe some other code here for specific errors?
        return res.status;
    }
}

export async function getUser( uid:number ) {
    
    const res = await fetch(`${BACKEND_URL}/users/${uid}/`, {
        headers: {
            "Content-Type": "application/json"
        },
    });

    if ( res.ok ) {

        return await res.json()
            .then(body => {
                return JSON.parse(body);
            });

    } else {
        // maybe some other code here for specific errors?
        return res.status;
    }
}

export async function searchUser( name:string ) {

    const res = await fetch( `${BACKEND_URL}//users/search/${name}/`, {
        method: "get",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
    });

    if (res.ok) {
        return await res.json()
            .then( body => {
                return JSON.parse(body);
            });
    }
    else {
        alert("ERROR GRABBING FEED: " + res.status);
    }
}

export async function getUserFeed( cuid:number, uid:number ) {

    const res = await fetch(`${BACKEND_URL}/users/${uid}/feed/`, {
        method: "get",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
    });

    if (res.ok) {
        return await res.json()
            .then(body => {
                return JSON.parse(body);
            });

    } else {
        alert("ERROR GRABBING FEED: " + res.status );
    }

}

export async function getWallet( token:string|null ) {

    if ( token==null ) return (403);

    const res = await fetch(`${BACKEND_URL}/wallet/`, {
        method: "get",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "token": token,
        },
    });

    if ( res.ok ) {
        return await res.json()
            .then( body => {
                return JSON.parse(body);
            })

    } else{
        alert("ERROR: " + res.status );
    }

}

export async function addToWallet( token:string|null, amount:string|null ) {
    if (amount === ""){
        alert("No value inserted!");
        return;
    }

    if ( token==null ) return (403);

    const res = await fetch(`${BACKEND_URL}/wallet/add/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "token": token,
        },
        body: JSON.stringify({ amount } )
    });

    if ( res.ok ) {
        return 200;
    } else {
        alert(" Error adding money to wallet ");
        return res.status;
    }
}

export async function createPost( token:string|null, title:string, contents:string ) {

    if ( token==null ) return (403);

    const res = await fetch(`${BACKEND_URL}/posts/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "token": token,
        },
        body: JSON.stringify({ title, contents })
    });

    if ( res.ok ) {
        return 200;
    } else {
        // maybe some other code here for specific errors?
        return res.status;
    }

}

export async function deletePost( token:string|null, pid:number ) {

    if ( token==null ) return (403);

    const res = await fetch(`${BACKEND_URL}/posts/${pid}/`, {
        method: "delete",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "token": token,
        },
    });

    if ( res.ok ) {
        alert("Succesfully deleted");
        return 200;
    } else{
        alert("ERROR: " + res.status );
    }
}

export async function addLike( token:string|null, pid:number ) {

    if ( token==null ) return (403);

    const res = await fetch(`${BACKEND_URL}/posts/${pid}/likes/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "token": token,
        },
        body: JSON.stringify({ token })
    });

    if (res.ok) {
        alert("Good?")
    } else {
        alert("Bad");
    }


}

export async function getLikes(token: string|null, pid: number) {

    if ( token==null ) return (403);

    const res = await fetch(`${BACKEND_URL}/posts/${pid}/likes/`, {
        method: "get",
        mode: "cors",
        headers: {
            "Content-Type": "application/json",
            "token" : token,
        },
        body: JSON.stringify({ token })
    });

}


let exports = {
    register,
    login,
    logout,
    getUserID,
    getUser,
    searchUser,
    createPost,
    deletePost,
    getWallet,
    addToWallet,
    getUserFeed,
    addLike,
    getLikes,
}

export default exports
