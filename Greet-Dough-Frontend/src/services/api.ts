export const BACKEND_URL = "http://localhost:5432";

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
    alert( "REQUEST: " + JSON.stringify( {email, password}) );

    const res = await fetch(`${BACKEND_URL}/login`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    alert(res)

    if ( res.ok ) {
        return ;
    } else {
        return false;
    }
}

let exports = {
    register,
    login
}

export default exports
