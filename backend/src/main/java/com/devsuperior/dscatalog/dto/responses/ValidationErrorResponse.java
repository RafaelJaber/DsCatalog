package com.devsuperior.dscatalog.dto.responses;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse extends CustomErrorResponse {

    private final List<FieldMessageResponse> messages = new ArrayList<>();

    public ValidationErrorResponse(OffsetDateTime timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    public void addError(String fieldName, String message) {
        messages.removeIf(x -> x.getFieldName().equals(fieldName));
        messages.add(new FieldMessageResponse(fieldName, message));
    }
}
