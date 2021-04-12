export const BACKEND_URL = "http://localhost:5432";

function getCurrentToken(): string {
    return localStorage.getItem("authToken") || "";
}

function setCurrentToken( token:string): void {
    localStorage.setItem("authToken", token);
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
        // set token here once i get it to work :): ):

    } else {
        // maybe some other code here for specific errors?
        return res.status;
    }
}

let exports = {
    register,
    login
}

export default exports
