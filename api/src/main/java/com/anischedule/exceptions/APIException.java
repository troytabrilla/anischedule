package com.anischedule.exceptions;

import java.util.List;

public class APIException extends BaseException {
    
    public APIException(String message, List<Object> details) {
        super(message, details);
    }

}
