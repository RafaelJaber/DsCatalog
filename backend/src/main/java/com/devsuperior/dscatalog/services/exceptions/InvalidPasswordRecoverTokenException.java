package com.devsuperior.dscatalog.services.exceptions;

public class InvalidPasswordRecoverTokenException extends RuntimeException {
    public InvalidPasswordRecoverTokenException() {
        super("Invalid password recover token");
    }
}
