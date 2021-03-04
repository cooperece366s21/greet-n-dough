package database;

import com.google.gson.Gson;
import spark.Request;

public class Handler {

    private final Server service;
    private final Gson gson;

    public Handler( Server service, Gson gson ) {

        this.service = service;
        this.gson = gson;

    }



}
