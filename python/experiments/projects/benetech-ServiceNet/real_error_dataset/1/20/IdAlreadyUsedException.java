package org.benetech.servicenet.errors;

import com.netflix.hystrix.exception.HystrixBadRequestException;;

public class IdAlreadyUsedException extends HystrixBadRequestException {

    public IdAlreadyUsedException() {
        super("Conflict");
    }
}
