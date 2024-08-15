package com.devsuperior.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, String locator, String value) {
        super(String.format("%s with %s %s not found. Please verify the %s and try again.",
                entityName, locator.toUpperCase(), value, locator.toUpperCase()));
    }
}
