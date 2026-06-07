package com.mediconnect.exceptions;
// Eccezione per richieste non valide (HTTP 400)

public class BadRequestException extends RuntimeException{
    public BadRequestException(String ex){
        super(ex);
    }
}
