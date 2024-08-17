package com.devsuperior.dscatalog.services.exceptions;

public class UniqueKeyDatabaseException extends RuntimeException {
    public UniqueKeyDatabaseException() {
        super("Database duplicated key error");
    }
}
