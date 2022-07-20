package com.eneskacan.bankingsystem.exception;

public class FailingApiCallException extends Exception {

    public FailingApiCallException(String errorMessage) {
        super(errorMessage);
    }
}
