package com.eneskacan.bankingsystem.exception;

public class DeletedAccountException extends Exception {

    public DeletedAccountException(String errorMessage) {
        super(errorMessage);
    }
}
