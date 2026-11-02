package com.educatedandproud.educated_and_proud_api.repository;

import com.educatedandproud.educated_and_proud_api.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.educatedandproud.educated_and_proud_api.testutil.StudentTestData.EMAIL;
import static com.educatedandproud.educated_and_proud_api.testutil.StudentTestData.student;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void existsByEmail_returnsTrue_whenEmailExists() {
        studentRepository.save(student(null, EMAIL));

        assertTrue(studentRepository.existsByEmail(EMAIL));
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailDoesNotExist() {
        assertFalse(studentRepository.existsByEmail("nobpdy@test.com"));
    }

    @Test
    void save_throwsDataIntegrityViolation_whenEmailIsDuplicate() {
        studentRepository.saveAndFlush(student(null, EMAIL));

        assertThrows(DataIntegrityViolationException.class,
                () -> studentRepository.saveAndFlush(student(null, EMAIL)));
    }

    @Test
    void save_setsIdAndCreatedAt_automatically() {
        Student saved = studentRepository.saveAndFlush(student(null, EMAIL));

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
    }
}
