import React from 'react';
import './Header.css';
import User from './User';

function Header() {

    return (
            <div className="header">

                <div className="inner left">
                    <div className="logo_container">
                        <h1>G&D</h1>
                    </div>
                </div>

                <div className="inner mid">
                    <ul className="table">
                        <a href=""> <li> Home </li> </a>
                        <a href=""> <li> Feed </li> </a>
                        <a href=""> <li> About </li> </a>

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

