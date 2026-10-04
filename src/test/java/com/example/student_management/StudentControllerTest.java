package com.example.student_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student("Alice", "alice@example.com", "Computer Science", 3.8);
        student.setId(1L);
    }

    @Test
    void createStudent_shouldReturn201() {
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        ResponseEntity<Student> response = studentController.createStudent(student);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Alice", response.getBody().getName());
        assertEquals("alice@example.com", response.getBody().getEmail());
    }

    @Test
    void getAllStudents_shouldReturnList() {
        List<Student> students = List.of(
                new Student("Bob", "bob@example.com", "Mathematics", 3.5),
                new Student("Carol", "carol@example.com", "Physics", 3.9)
        );
        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = studentController.getAllStudents();

        assertEquals(2, result.size());
        assertEquals("Bob", result.get(0).getName());
    }

    @Test
    void getStudentById_shouldReturn200WhenFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        ResponseEntity<Student> response = studentController.getStudentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice", response.getBody().getName());
    }

    @Test
    void getStudentById_shouldReturn404WhenNotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Student> response = studentController.getStudentById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateStudent_shouldReturn200WhenFound() {
        Student updated = new Student("Alice Updated", "alice.updated@example.com", "IT", 3.9);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<Student> response = studentController.updateStudent(1L, updated);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice Updated", response.getBody().getName());
        assertEquals("alice.updated@example.com", response.getBody().getEmail());
        assertEquals("IT", response.getBody().getDepartment());
        assertEquals(3.9, response.getBody().getCgpa());
    }

    @Test
    void updateStudent_shouldReturn404WhenNotFound() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Student> response = studentController.updateStudent(999L, student);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteStudent_shouldReturn204WhenFound() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = studentController.deleteStudent(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteStudent_shouldReturn404WhenNotFound() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        ResponseEntity<Void> response = studentController.deleteStudent(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(studentRepository, never()).deleteById(any());
    }
}
