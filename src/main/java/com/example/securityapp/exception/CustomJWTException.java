package com.example.securityapp.exception;

public class CustomJWTException extends RuntimeException {

    public CustomJWTException() {
        super();        
    }

    public CustomJWTException(String message) {
        super(message);        
    }

}
