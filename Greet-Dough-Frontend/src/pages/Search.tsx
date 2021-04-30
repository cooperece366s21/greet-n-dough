import React from 'react'
import { useParams } from "react-router-dom";
import Header from "../components/globalComponents/Header";
import SearchPageWrapper from "../components/searchComponents/SearchPageWrapper";


function Search(){

    const { name } = useParams<{ name: string }>();

    return(
        <>
            <Header />
            <SearchPageWrapper name={name} />
        </>
    )

}

export default Search;
