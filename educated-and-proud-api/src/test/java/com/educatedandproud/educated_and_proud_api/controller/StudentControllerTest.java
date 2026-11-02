package com.educatedandproud.educated_and_proud_api.controller;

import com.educatedandproud.educated_and_proud_api.dto.StudentRequest;
import com.educatedandproud.educated_and_proud_api.dto.StudentResponse;
import com.educatedandproud.educated_and_proud_api.dto.StudentUpdateRequest;
import com.educatedandproud.educated_and_proud_api.exception.DuplicateEmailException;
import com.educatedandproud.educated_and_proud_api.exception.StudentNotFoundException;
import com.educatedandproud.educated_and_proud_api.model.Student;
import com.educatedandproud.educated_and_proud_api.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.educatedandproud.educated_and_proud_api.testutil.StudentTestData.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @Test
    void createStudent_returns200_whenRequestBodyIsValid() throws Exception {
        when(studentService.createStudent(any())).thenReturn(studentResponse(1L, EMAIL));

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(studentRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void createStudent_returns400_whenFirstNameIsBlank() throws Exception {
        StudentRequest wrong = new StudentRequest("", "Hilfiger", EMAIL,
                "San Diego City College", "SDSU",
                2028, "Business");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(wrong)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    void createStudent_returns400_whenEmailIsInvalid() throws Exception {
        StudentRequest wrong = new StudentRequest("Tommie","Hilfiger", "notanemail",
                "San Diego City College", "SDSU",
                2028, "Business");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(wrong)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    void createStudent_return400_whenGradYearIsMissing() throws Exception {
        StudentRequest wrong = new StudentRequest("Tommie", "Hilfiger", EMAIL,
                "San Diego City College", "SDSU",
                null, "Business");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(wrong)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    void createStudent_returns409_whenEmailIsDuplicated() throws Exception {
        when(studentService.createStudent(any())).thenThrow(new DuplicateEmailException(EMAIL));

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(studentRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString(EMAIL)));
    }

    @Test
    void getStudents_returns200_withList() throws Exception {
        when(studentService.getAllStudents())
                .thenReturn(List.of(studentResponse(1L, "one@test.com"),
                        studentResponse(2L, "two@test.com")));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("one@test.com"));
    }

    @Test
    void getStudentById_returns200_whenFound() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(studentResponse(1L, EMAIL)));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getStudentById_returns404_whenNotFound() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_returns200_whenRequestBodyIsValid() throws Exception {
        when(studentService.updateStudent(eq(1L), any())).thenReturn(studentResponse(1L, "new@test.com"));

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(studentUpdateRequest("new@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void updateStudent_returns400_whenEmailIsBlank() throws Exception {
        StudentUpdateRequest wrong = new StudentUpdateRequest("",
                "San Diego City College", "SDSU", 2028, "Business");

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(wrong)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    void updateStudent_returns404_whenStudentNotFound() throws Exception {
        when(studentService.updateStudent(eq(99L), any()))
                .thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(put("/api/students/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(studentUpdateRequest(EMAIL))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("99")));
    }

    @Test
    void updateStudent_returns409_whenEmailIsTaken() throws Exception {
        when(studentService.updateStudent(eq(1L), any()))
                .thenThrow(new DuplicateEmailException("taken@test.com"));

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(studentUpdateRequest("taken@test.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteStudent_returns204_whenSuccess() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudent(1L);
    }

    @Test
    void deleteStudent_returns404_whenNotFound() throws Exception {
        doThrow(new StudentNotFoundException(99L)).when(studentService).deleteStudent(99L);

        mockMvc.perform(delete("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("99")));
    }
}
