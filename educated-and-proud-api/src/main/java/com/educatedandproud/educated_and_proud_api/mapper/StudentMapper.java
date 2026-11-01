package com.educatedandproud.educated_and_proud_api.mapper;

import com.educatedandproud.educated_and_proud_api.dto.StudentRequest;
import com.educatedandproud.educated_and_proud_api.dto.StudentResponse;
import com.educatedandproud.educated_and_proud_api.dto.StudentUpdateRequest;
import com.educatedandproud.educated_and_proud_api.model.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest studentRequest) {
        Student student = new Student();
        student.setFirstName(studentRequest.firstName());
        student.setLastName((studentRequest.lastName()));
        student.setEmail(studentRequest.email());
        student.setSchoolAttending(studentRequest.schoolAttending());
        student.setSchoolTransferringTo(studentRequest.schoolTransferringTo());
        student.setEstimatedGradYear(studentRequest.estimatedGradYear());
        student.setMajor(studentRequest.major());
        return student;
    }

    public void updateEntity(Student student, StudentUpdateRequest studentUpdateRequest) {
        student.setEmail(studentUpdateRequest.email());
        student.setSchoolAttending(studentUpdateRequest.schoolAttending());
        student.setSchoolTransferringTo(studentUpdateRequest.schoolTransferringTo());
        student.setEstimatedGradYear(studentUpdateRequest.estimatedGradYear());
        student.setMajor(studentUpdateRequest.major());
    }

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getSchoolAttending(),
                student.getSchoolTransferringTo(),
                student.getEstimatedGradYear(),
                student.getMajor(),
                student.getCreatedAt()
        );
    }
}
