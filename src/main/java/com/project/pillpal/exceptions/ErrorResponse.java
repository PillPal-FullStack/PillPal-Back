package com.project.pillpal.exceptions;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    String message;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    Map<String, String> details;

    public ErrorResponse(String message, String details, String requestURI, int status) {
        this.message = message;
        this.details = Map.of("error", details, "path", requestURI, "status", String.valueOf(status));
        this.timestamp = LocalDateTime.now();
    }
}
