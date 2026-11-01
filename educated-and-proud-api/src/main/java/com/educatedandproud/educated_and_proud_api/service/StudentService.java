package com.educatedandproud.educated_and_proud_api.service;

import com.educatedandproud.educated_and_proud_api.dto.StudentRequest;
import com.educatedandproud.educated_and_proud_api.dto.StudentResponse;
import com.educatedandproud.educated_and_proud_api.dto.StudentUpdateRequest;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentResponse createStudent(StudentRequest studentRequest);
    Optional<StudentResponse> getStudentById(Long id);
    List<StudentResponse> getAllStudents();
    StudentResponse updateStudent(Long id, StudentUpdateRequest studentUpdateRequest);
    void deleteStudent(Long id);
}
