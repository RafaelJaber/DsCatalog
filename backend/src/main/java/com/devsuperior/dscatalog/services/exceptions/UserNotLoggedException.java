package com.devsuperior.dscatalog.services.exceptions;

public class UserNotLoggedException extends RuntimeException {
    public UserNotLoggedException(String message) {
        super(message);
    }
}
