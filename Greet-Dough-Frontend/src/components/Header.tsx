import React from 'react';
import './Header.css';
import User from './User';
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from "react-router-dom";

function Header() {

    return (
            <div className="header">

                <div className="inner left">
                    <div className="logo_container">
                        <h1>G&D</h1>
                    </div>
                </div>

                <div className="inner mid">
                    <ul className="navigation">

                        <Link to="/"> <li> Home </li></Link>
                        <Link to="/feed"> <li> Feed </li></Link>
                        <Link to="/about"> <li> About </li></Link>

                        <div className="search_container">
                            <div className="search">
                                <input type="text" placeholder="Search for user...">
                                </input>
                            </div>
                        </div>

                    </ul>
                </div>

                <div className="inner right">
                    <User/>
                </div>

            </div>
    )

}


export default Header

