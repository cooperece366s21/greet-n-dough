export const BACKEND_URL = "http://localhost:5432";

function getCurrentToken(): string {
    return localStorage.getItem("authToken") || "";
}

function setCurrentToken( token:string): void {
    localStorage.setItem("authToken", token);
}

export async function getUserID(): Promise<number> {
    let authToken = getCurrentToken();

    if (authToken === "") {
        alert("NO TOKEN IN BROWSER!") // debug
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
        let uid = await res.json()
            .then(json => JSON.parse(json))
            .then(parsed => { return parsed.uid } );

        if( uid === -1 ) {
            alert("token valid but user is -1?!?!?!") // debug
            return -1;
        }

        return uid;
    }

    else {
        alert( "ERROR: " + res.status );
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

    alert( JSON.stringify({ email, password } ) )

    const res = await fetch(`${BACKEND_URL}/login/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });



    if ( res.ok ) {
        res.json()
            .then( body => {
                setCurrentToken( JSON.parse(body).authToken )
                // alert("body: " + body);
                // alert("Token: " + JSON.parse(body).authToken );
            })

        return 200;

    } else {
        // maybe some other code here for specific errors?
        return res.status;
    }
}

let exports = {
    register,
    login,
    getUserID,
}

export default exports
