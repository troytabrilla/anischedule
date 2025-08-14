package com.anischedule.exceptions;


import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExceptionTests {
    
    @Test
    public void apiExceptionHasDetails() {
        List<Object> details = Arrays.asList("details");
        BaseException e = new APIException("test", details);

        Assertions.assertEquals(details, e.getDetails());
        Assertions.assertTrue(e.toString().contains("test - [details]"));
    }

    @Test
    public void apiExceptionHasEmptyDetails() {
        List<Object> details = Arrays.asList();
        BaseException e = new APIException("test", details);

        Assertions.assertEquals(details, e.getDetails());
        Assertions.assertTrue(e.toString().contains("test - []"));
    }

    @Test
    public void apiExceptionHasNullDetails() {
        BaseException e = new APIException("test", null);

        Assertions.assertEquals(null, e.getDetails());
        Assertions.assertTrue(e.toString().contains("test - null"));
    }

    @Test
    public void badRequestExceptionHasDetails() {
        List<Object> details = Arrays.asList("details");
        BaseException e = new BadRequestException("test", details);

        Assertions.assertEquals(details, e.getDetails());
        Assertions.assertTrue(e.toString().contains("test - [details]"));
    }

    @Test
    public void badRequestExceptionHasEmptyDetails() {
        List<Object> details = Arrays.asList();
        BaseException e = new BadRequestException("test", details);

        Assertions.assertEquals(details, e.getDetails());
        Assertions.assertTrue(e.toString().contains("test - []"));
    }

    @Test
    public void badRequestExceptionHasNullDetails() {
        BaseException e = new BadRequestException("test", null);

        Assertions.assertEquals(null, e.getDetails());
        Assertions.assertTrue(e.toString().contains("test - null"));
    }

}
