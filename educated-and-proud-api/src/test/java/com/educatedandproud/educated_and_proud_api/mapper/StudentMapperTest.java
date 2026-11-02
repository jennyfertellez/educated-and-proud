package com.educatedandproud.educated_and_proud_api.mapper;

import com.educatedandproud.educated_and_proud_api.dto.StudentResponse;
import com.educatedandproud.educated_and_proud_api.model.Student;
import com.educatedandproud.educated_and_proud_api.testutil.StudentTestData;
import org.junit.jupiter.api.Test;

import static com.educatedandproud.educated_and_proud_api.testutil.StudentTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StudentMapperTest {

    private final StudentMapper studentMapper = new StudentMapper();

    @Test
    void toEntity_mapsAllRequestFields() {
        Student result = studentMapper.toEntity(studentRequest());

        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertEquals("Tommie", result.getFirstName());
        assertEquals("Hilfiger", result.getLastName());
        assertEquals(EMAIL, result.getEmail());
        assertEquals("San Diego City College", result.getSchoolAttending());
        assertEquals("SDSU", result.getSchoolTransferringTo());
        assertEquals(2028, result.getEstimatedGradYear());
        assertEquals("Business", result.getMajor());
    }

    @Test
    void toResponse_mapAllEntityFields() {
        StudentResponse result = studentMapper.toResponse(student(1L, EMAIL));

        assertEquals(studentResponse(1L, EMAIL), result);
    }

    @Test
    void updateEntity_changsEditableFields_andLeavesLockedFieldsAlone() {
        Student existing = student(1L, EMAIL);

        studentMapper.updateEntity(existing, studentUpdateRequest("new@test.com"));

        assertEquals("new@test.com", existing.getEmail());
        assertEquals(1L, existing.getId());
        assertEquals("Tommie", existing.getFirstName());
        assertEquals("Hilfiger", existing.getLastName());
    }
}
