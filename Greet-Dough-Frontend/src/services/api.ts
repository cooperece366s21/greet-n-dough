export const BACKEND_URL = "http://localhost:5432";

export async function register(  name:string ) {

    // alert( "JSON BODY IS THIS: " + JSON.stringify({ name }));

    const res = await fetch(`${BACKEND_URL}/users/`, {
        method: "post",
        mode: "cors",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ name })
    });

    if ( res.ok ) {
        return 200;
    } else {
        alert("Response: " + JSON.stringify(res) );
        return -1;
    }

}

let exports = {
    register
}

export default exports
