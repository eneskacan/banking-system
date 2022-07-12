package com.eneskacan.bankingsystem.error;

import lombok.Data;

import java.util.Map;

/**
 * https://medium.com/@sampathsl/exception-handling-for-rest-api-with-spring-boot-c5d5ba928f5b
 */

@Data
public class ExceptionResponse {

    private Integer status;
    private String path;
    private String message;
    private String timestamp;
    private String trace;

    public ExceptionResponse(int status, Map<String, Object> errorAttributes) {
        this.setStatus(status);
        this.setPath((String) errorAttributes.get("path"));
        this.setMessage((String) errorAttributes.get("message"));
        this.setTimestamp(errorAttributes.get("timestamp").toString());
        this.setTrace((String) errorAttributes.get("trace"));
    }
}