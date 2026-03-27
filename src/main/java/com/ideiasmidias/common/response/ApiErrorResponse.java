package com.ideiasmidias.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;
    private String message;
    private Integer status;
    private String code;
    private String path;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
}