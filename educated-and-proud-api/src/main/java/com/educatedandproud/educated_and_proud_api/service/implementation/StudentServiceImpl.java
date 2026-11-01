package com.educatedandproud.educated_and_proud_api.service.implementation;

import com.educatedandproud.educated_and_proud_api.exception.StudentNotFoundException;
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

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        existingStudent.setEmail(student.getEmail());
        existingStudent.setSchoolAttending(student.getSchoolAttending());
        existingStudent.setSchoolTransferringTo(student.getSchoolTransferringTo());
        existingStudent.setEstimatedGradYear(student.getEstimatedGradYear());
        existingStudent.setMajor(student.getMajor());

        return studentRepository.save(existingStudent);

    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.deleteById(id);
    }
}
