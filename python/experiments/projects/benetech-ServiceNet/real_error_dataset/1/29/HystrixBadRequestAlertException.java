package org.benetech.servicenet.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.Getter;

import java.net.URI;

public class HystrixBadRequestAlertException extends HystrixBadRequestException {

    private static final long serialVersionUID = 1L;

    @Getter
    private final BadRequestAlertException cause;

    @JsonCreator
    public HystrixBadRequestAlertException(
        @JsonProperty("entityName") String entityName,
        @JsonProperty("errorKey") String errorKey,
        @JsonProperty("type") URI type,
        @JsonProperty("title") String title,
        @JsonProperty("status") Integer status,
        @JsonProperty("message") String message,
        @JsonProperty("params") String params)
    {
        super(message);
        this.cause = new BadRequestAlertException(
            entityName, errorKey, type, title, status, message, params);
    }

}
