package com.educatedandproud.educated_and_proud_api.testutil;

import com.educatedandproud.educated_and_proud_api.dto.StudentRequest;
import com.educatedandproud.educated_and_proud_api.dto.StudentResponse;
import com.educatedandproud.educated_and_proud_api.dto.StudentUpdateRequest;
import com.educatedandproud.educated_and_proud_api.model.Student;

public final class StudentTestData {

    public static final String EMAIL = "tommie@test.com";

    private StudentTestData() {}

    public static StudentRequest studentRequest() {
        return new StudentRequest("Tommie", "Hilfiger", EMAIL,
                "San Diego City College", "SDSU", 2028, "Business");
    }

    public static StudentUpdateRequest studentUpdateRequest(String email) {
        return new StudentUpdateRequest(email, "San Diego City College", "SDSU",
                2028, "Business");
    }

    public static Student student(Long id, String email) {
        Student student = new Student();
        student.setId(id);
        student.setFirstName("Tommie");
        student.setLastName("Hilfiger");
        student.setEmail(email);
        student.setSchoolAttending("San Diego City College");
        student.setSchoolTransferringTo("SDSU");
        student.setEstimatedGradYear(2028);
        student.setMajor("Business");
        return student;
    }

    public static StudentResponse studentResponse(Long id, String email) {
        return new StudentResponse(id, "Tommie", "Hilfiger", email,
                "San Diego City College", "SDSU", 2028,
                "Business", null);
    }
}
