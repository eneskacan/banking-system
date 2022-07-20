package com.eneskacan.bankingsystem.exception;

public class InvalidAccountTypeException extends Exception {

    public InvalidAccountTypeException(String errorMessage) {
        super(errorMessage);
    }
}
