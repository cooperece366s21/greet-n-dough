import React from 'react';
import './User.css';
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from "react-router-dom";



function User() {
    const isLoggedIn = checkIfLogged()
    const user = "USER_ONE";


    if ( isLoggedIn ) return (

        <div className="User">

            <div className="image-container">
                <img src="https://serc.carleton.edu/download/images/54334/empty_user_icon_256.v2.png">
                </img>
            </div>

            <div className="text-container">

                <div className="welcome-container">
                    <span className="bold"> Welcome, </span>  {user}
                </div>

                <div className="logout-container">
                    <span onClick={logout}> Logout </span>
                </div>

            </div>

        </div>

    );
    else return (
        <div className="User">

            <div className="text-container">

                <div className="welcome-container">
                    <span className="bold"> User not logged in </span>
                </div>

                <div className="logout-container">


                        <span style={{ marginRight: "20px" }}>
                            <Link to="/login"> Login </Link>
                        </span>

                        <span>
                            <Link to="/register"> Register </Link>
                        </span>

                </div>

            </div>


        </div>
    );
}

function checkIfLogged(){
    // API code to check if someone is logged in here!
    return false;
}

function logout(){
    alert("Logging out!");
}


export default User;
