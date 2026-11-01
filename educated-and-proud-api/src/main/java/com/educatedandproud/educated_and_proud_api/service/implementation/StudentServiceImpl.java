package com.educatedandproud.educated_and_proud_api.service.implementation;

import com.educatedandproud.educated_and_proud_api.dto.StudentRequest;
import com.educatedandproud.educated_and_proud_api.dto.StudentResponse;
import com.educatedandproud.educated_and_proud_api.dto.StudentUpdateRequest;
import com.educatedandproud.educated_and_proud_api.exception.DuplicateEmailException;
import com.educatedandproud.educated_and_proud_api.exception.StudentNotFoundException;
import com.educatedandproud.educated_and_proud_api.mapper.StudentMapper;
import com.educatedandproud.educated_and_proud_api.model.Student;
import com.educatedandproud.educated_and_proud_api.repository.StudentRepository;
import com.educatedandproud.educated_and_proud_api.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponse createStudent(StudentRequest studentRequest) {
        if (studentRepository.existsByEmail(studentRequest.email())) {
            throw new DuplicateEmailException(studentRequest.email());
        }
        Student saved = studentRepository.save(studentMapper.toEntity(studentRequest));
        return studentMapper.toResponse(saved);
    }

    @Override
    public Optional<StudentResponse> getStudentById(Long id) {
        return studentRepository.findById(id).map(studentMapper::toResponse);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentUpdateRequest studentUpdateRequest) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        boolean emailChanged = !existingStudent.getEmail().equalsIgnoreCase(studentUpdateRequest.email());
        if (emailChanged && studentRepository.existsByEmail(studentUpdateRequest.email())) {
            throw new DuplicateEmailException(studentUpdateRequest.email());
        }

        studentMapper.updateEntity(existingStudent, studentUpdateRequest);
        return studentMapper.toResponse(studentRepository.save(existingStudent));
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.deleteById(id);
    }
}
