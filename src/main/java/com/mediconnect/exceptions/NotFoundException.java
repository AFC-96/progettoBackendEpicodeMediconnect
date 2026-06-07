package com.mediconnect.exceptions;
// Eccezione per risorse non trovate (HTTP 404)

public class NotFoundException extends RuntimeException{
    public NotFoundException(String ex){
        super(ex);
    }
}
