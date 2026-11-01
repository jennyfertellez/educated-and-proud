package com.educatedandproud.educated_and_proud_api.exception;

public class DuplicateEmailException  extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A student with email " + email + " already exists");
    }
}
