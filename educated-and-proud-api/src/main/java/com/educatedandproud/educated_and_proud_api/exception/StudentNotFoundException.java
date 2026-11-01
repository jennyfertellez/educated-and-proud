package com.educatedandproud.educated_and_proud_api.exception;

public class StudentNotFoundException  extends RuntimeException {

    public StudentNotFoundException(Long id) {
        super("Student not found with id: " + id);
    }
}
