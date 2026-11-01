package com.educatedandproud.educated_and_proud_api.service;

import com.educatedandproud.educated_and_proud_api.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    Student createStudent(Student student);
    Optional<Student> getStudentById(Long id);
    List<Student> getAllStudents();
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
}
