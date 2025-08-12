package com.anischedule.exceptions;

import java.util.List;

public class BadRequestException extends BaseException {

    public BadRequestException(String message, List<Object> details) {
        super(message, details);
    }

}
