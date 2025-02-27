package com.example.airbnb.airbnb_app.exception;

public class UnAuthorisedException extends RuntimeException{
    public UnAuthorisedException(String message){
        super(message);
    }
}
