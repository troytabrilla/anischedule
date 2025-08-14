package com.anischedule.exceptions;

import java.util.List;

public abstract class BaseException extends Exception {

    private final List<Object> details;

    public BaseException(String message, List<Object> details) {
        super(message);
        this.details = details;
    }

    public List<Object> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + this.details;
    }

}
