package com.devsuperior.dscatalog.dto.responses.errors;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse extends CustomErrorResponse {

    private final List<FieldMessageResponse> errors = new ArrayList<>();

    public ValidationErrorResponse(OffsetDateTime timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    public void addError(String fieldName, String message) {
        errors.removeIf(x -> x.getFieldName().equals(fieldName));
        errors.add(new FieldMessageResponse(fieldName, message));
    }
}
