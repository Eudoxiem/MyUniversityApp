package com.myuniversity.app.exception;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private Instant timestamp;
    private Map<String, String> errors;
}
