package com.devsuperior.dscatalog.dto.responses.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {
    private OffsetDateTime timestamp;
    private Integer httpStatus;
    private String httpError;
    private String message;
    private String path;
}
