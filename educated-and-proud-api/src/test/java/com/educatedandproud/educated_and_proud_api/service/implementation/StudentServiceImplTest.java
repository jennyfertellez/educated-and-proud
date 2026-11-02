package com.educatedandproud.educated_and_proud_api.service.implementation;

import com.educatedandproud.educated_and_proud_api.dto.StudentRequest;
import com.educatedandproud.educated_and_proud_api.dto.StudentResponse;
import com.educatedandproud.educated_and_proud_api.dto.StudentUpdateRequest;
import com.educatedandproud.educated_and_proud_api.exception.DuplicateEmailException;
import com.educatedandproud.educated_and_proud_api.exception.StudentNotFoundException;
import com.educatedandproud.educated_and_proud_api.mapper.StudentMapper;
import com.educatedandproud.educated_and_proud_api.model.Student;
import com.educatedandproud.educated_and_proud_api.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.educatedandproud.educated_and_proud_api.testutil.StudentTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void createStudent_savesAndReturnsResponse_whenEmailIsNew() {
        StudentRequest studentRequest = studentRequest();
        Student entity = student(null, EMAIL);
        Student saved = student(1L, EMAIL);
        StudentResponse expectedResponse = studentResponse(1L, EMAIL);

        when(studentRepository.existsByEmail("tommie@test.com")).thenReturn(false);
        when(studentMapper.toEntity(studentRequest)).thenReturn(entity);
        when(studentRepository.save(entity)).thenReturn(saved);
        when(studentMapper.toResponse(saved)).thenReturn(expectedResponse);

        StudentResponse actualResponse = studentService.createStudent(studentRequest);

        assertEquals(expectedResponse, actualResponse);
        verify(studentRepository).save(entity);
    }

    @Test
    void createStudent_throwsDuplicateEmailException_whenEmailIsAlreadyExists() {
        StudentRequest studentRequest =  studentRequest();
        when(studentRepository.existsByEmail("tommie@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> studentService.createStudent(studentRequest));

        verify(studentRepository, never()).save(any());
    }

    @Test
    void getStudentById_returnsResponse_whenStudentExists() {
        Student student = student(1L, EMAIL);
        StudentResponse expectedResponse = studentResponse(1L, EMAIL);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toResponse(student)).thenReturn(expectedResponse);

        Optional<StudentResponse> actualResponse = studentService.getStudentById(1L);

        assertTrue(actualResponse.isPresent());
        assertEquals(expectedResponse, actualResponse.get());
    }

    @Test
    void getStudentById_returnsEmptyResponse_whenStudentDoesNotExist() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<StudentResponse> actualResponse = studentService.getStudentById(99L);

        assertTrue(actualResponse.isEmpty());
        verifyNoInteractions(studentMapper);
    }

    @Test
    void getAllStudents_returnsResponse_whenStudentExists() {
        Student first = student(1L, "first@test.com");
        Student second = student(2L, "second@test.com");
        StudentResponse firstResponse = studentResponse(1L, "first@test.com");
        StudentResponse secondResponse = studentResponse(2L, "second@test.com");

        when(studentRepository.findAll()).thenReturn(List.of(first, second));
        when(studentMapper.toResponse(first)).thenReturn(firstResponse);
        when(studentMapper.toResponse(second)).thenReturn(secondResponse);

        List<StudentResponse> actualResponse = studentService.getAllStudents();

        assertEquals(2, actualResponse.size());
        assertEquals(List.of(firstResponse, secondResponse), actualResponse);
    }

    @Test
    void getAllStudents_returnsEmptyList_whenNoStudentsExist() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<StudentResponse> actualResponse = studentService.getAllStudents();

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isEmpty());
        verifyNoInteractions(studentMapper);
    }

    @Test
    void updateStudent_throwsStudentNotFoundException_whenIdDoesNotExist() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class,
                () -> studentService.updateStudent(99L, studentUpdateRequest(EMAIL)));

        verify(studentRepository, never()).save(any());
        verifyNoInteractions(studentMapper);
    }

    @Test
    void updateStudent_updatesWithoutEmailCheck_whenEmailIsUnchanged() {
        Student existing = student(1L, EMAIL);
        StudentUpdateRequest studentUpdateRequest = studentUpdateRequest(EMAIL);
        StudentResponse expectedResponse = studentResponse(1L, EMAIL);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);
        when(studentMapper.toResponse(existing)).thenReturn(expectedResponse);

        StudentResponse actualResponse = studentService.updateStudent(1L, studentUpdateRequest);

        assertEquals(expectedResponse, actualResponse);
        verify(studentRepository, never()).existsByEmail(any());
        verify(studentMapper).updateEntity(existing, studentUpdateRequest);
        verify((studentRepository)).save(existing);
    }

    @Test
    void updateStudent_updates_whenEmailChangedToUnusedEmail() {
        Student existing = student(1L, EMAIL);
        StudentUpdateRequest studentUpdateRequest = studentUpdateRequest("update@test.com");
        StudentResponse expectedResponse = studentResponse(1L, "update@test.com");

        when((studentRepository.findById(1L))).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmail("update@test.com")).thenReturn(false);
        when((studentRepository.save(existing))).thenReturn(existing);
        when(studentMapper.toResponse(existing)).thenReturn(expectedResponse);

        StudentResponse actualResponse = studentService.updateStudent(1L, studentUpdateRequest);

        assertEquals(expectedResponse, actualResponse);
        verify(studentMapper).updateEntity(existing, studentUpdateRequest);
        verify((studentRepository)).save(existing);
    }

    @Test
    void updateStudent_throwsDuplicateEmailExcpetion_whenEmailChangedToTakenEmail() {
        Student existing = student(1L, EMAIL);
        StudentUpdateRequest studentUpdateRequest = studentUpdateRequest("taken@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> studentService.updateStudent(1L, studentUpdateRequest));

        verify(studentRepository, never()).save(any());
        verify(studentMapper, never()).updateEntity(any(), any());
    }

    @Test
    void updateStudent_treatsEmailAsUnchanged_whenOnlyCaseDiffers() {
        Student existing = student(1L, "Tommie@test.com");
        StudentUpdateRequest request = studentUpdateRequest("tommie@test.com");
        StudentResponse expected = studentResponse(1L, "tommie@test.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);
        when(studentMapper.toResponse(existing)).thenReturn(expected);

        studentService.updateStudent(1L, request);

        verify(studentRepository, never()).existsByEmail(any());
        verify(studentRepository).save(existing);
    }

    @Test
    void deleteStudent_deletes_whenStudentExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student(1L, EMAIL)));

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudent_throwsStudentNotFoundException_whenIdDoesNotExist() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class,
                () -> studentService.deleteStudent(99L));

        verify(studentRepository, never()).deleteById(anyLong());
    }
}
