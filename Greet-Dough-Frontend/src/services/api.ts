export const BACKEND_URL = "http://localhost:5432";

export async function register(  email:string, username:string, password:string ) {

    alert( "JSON BODY IS THIS: " + JSON.stringify({ email, username, password }));

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
        return -1;
    }

}

let exports = {
    register
}

export default exports
