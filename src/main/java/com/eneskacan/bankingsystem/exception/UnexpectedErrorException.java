package com.eneskacan.bankingsystem.exception;

public class UnexpectedErrorException extends Exception {

    public UnexpectedErrorException(String errorMessage) {
        super(errorMessage);
    }
}
