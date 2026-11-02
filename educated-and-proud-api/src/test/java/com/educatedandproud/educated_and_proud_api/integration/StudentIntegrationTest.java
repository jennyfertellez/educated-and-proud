package com.educatedandproud.educated_and_proud_api.integration;

import com.educatedandproud.educated_and_proud_api.dto.StudentRequest;
import com.educatedandproud.educated_and_proud_api.dto.StudentUpdateRequest;
import com.educatedandproud.educated_and_proud_api.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.educatedandproud.educated_and_proud_api.testutil.StudentTestData.EMAIL;
import static com.educatedandproud.educated_and_proud_api.testutil.StudentTestData.studentRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class StudentIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void cleanDatabase() {
        studentRepository.deleteAll();
    }

    private long createStudent(StudentRequest body) throws Exception {
        String response = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void createThenGet_returnsSameStudent() throws Exception {
        long id = createStudent(studentRequest());

        mockMvc.perform(get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.firstName").value("Tommie"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createTwice_withSameEmail_returns409() throws Exception {
        createStudent(studentRequest());

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    void update_thenGet_showsChangedMajor_andUnchangedName() throws Exception {
        long id = createStudent(studentRequest());
        StudentUpdateRequest update = new StudentUpdateRequest(
                EMAIL, "San Diego City College", "SDSU",
                2028, "Computer Science");

        mockMvc.perform(put("/api/students/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/students/" + id))
                .andExpect(jsonPath("$.major").value("Computer Science"))
                .andExpect(jsonPath("$.firstName").value("Tommie"))
                .andExpect(jsonPath("$.lastName").value("Hilfiger"));
    }

    @Test
    void delete_thenGet_returns404() throws Exception {
        long id = createStudent(studentRequest());

        mockMvc.perform(delete("/api/students/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/students/" + id))
                .andExpect(status().isNotFound());
    }
}
