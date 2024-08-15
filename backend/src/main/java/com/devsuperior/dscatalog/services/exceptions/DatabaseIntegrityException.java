package com.devsuperior.dscatalog.services.exceptions;

public class DatabaseIntegrityException extends RuntimeException{
    public DatabaseIntegrityException() {
        super("Referential integrity failure");
    }
}
