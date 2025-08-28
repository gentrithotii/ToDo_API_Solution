package se.lexicon.todo_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.todo_app.dto.TodoDto;
import se.lexicon.todo_app.entity.Person;
import se.lexicon.todo_app.entity.Todo;
import se.lexicon.todo_app.repository.TodoRepository;
import se.lexicon.todo_app.repository.PersonRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo todo;
    private TodoDto todoDto;
    private Person person;
    private final Long TEST_TODO_ID = 1L;
    private final Long TEST_PERSON_ID = 1L;
    private final String TEST_TITLE = "Test Todo";
    private final String TEST_DESCRIPTION = "Test Description";
    private final LocalDateTime TEST_DUE_DATE = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setUp() {
        person = new Person(TEST_PERSON_ID, "John Doe", "john@example.com", LocalDate.now(), null);
        todo = new Todo(TEST_TITLE, TEST_DESCRIPTION, false, TEST_DUE_DATE);
        todo.setId(TEST_TODO_ID);
        todo.setPerson(person);

        todoDto = TodoDto.builder()
                .id(TEST_TODO_ID)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .completed(false)
                .dueDate(TEST_DUE_DATE)
                .personId(TEST_PERSON_ID)
                .build();
    }

    @Test
    void testCreate() {
        // Arrange
        when(personRepository.findById(TEST_PERSON_ID)).thenReturn(Optional.of(person));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Act
        TodoDto created = todoService.create(todoDto);

        // Assert
        assertNotNull(created);
        assertEquals(TEST_TITLE, created.title());
        assertEquals(TEST_DESCRIPTION, created.description());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void testFindById() {
        // Arrange
        when(todoRepository.findById(TEST_TODO_ID)).thenReturn(Optional.of(todo));

        // Act
        TodoDto found = todoService.findById(TEST_TODO_ID);

        // Assert
        assertNotNull(found);
        assertEquals(TEST_TODO_ID, found.id());
        assertEquals(TEST_TITLE, found.title());
        verify(todoRepository).findById(TEST_TODO_ID);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(todoRepository.findById(TEST_TODO_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> todoService.findById(TEST_TODO_ID));
        verify(todoRepository).findById(TEST_TODO_ID);
    }

    @Test
    void testFindAll() {
        // Arrange
        Todo todo2 = new Todo("Second Todo", "Second Description", false, TEST_DUE_DATE);
        todo2.setId(2L);
        when(todoRepository.findAll()).thenReturn(Arrays.asList(todo, todo2));

        // Act
        List<TodoDto> result = todoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TEST_TITLE, result.get(0).title());
        verify(todoRepository).findAll();
    }

    @Test
    void testUpdate() {
        // Arrange
        String updatedTitle = "Updated Todo";
        String updatedDescription = "Updated Description";
        TodoDto updateDto = TodoDto.builder()
                .id(TEST_TODO_ID)
                .title(updatedTitle)
                .description(updatedDescription)
                .completed(true)
                .dueDate(TEST_DUE_DATE)
                .personId(TEST_PERSON_ID)
                .build();

        Todo updatedTodo = new Todo(updatedTitle, updatedDescription, true, TEST_DUE_DATE);
        updatedTodo.setId(TEST_TODO_ID);
        updatedTodo.setPerson(person);

        when(todoRepository.findById(TEST_TODO_ID)).thenReturn(Optional.of(todo));
        when(personRepository.findById(TEST_PERSON_ID)).thenReturn(Optional.of(person));
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

        // Act
        TodoDto result = todoService.update(TEST_TODO_ID, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedTitle, result.title());
        assertEquals(updatedDescription, result.description());
        assertTrue(result.completed());
        assertEquals(TEST_PERSON_ID, result.personId());
        verify(todoRepository).findById(TEST_TODO_ID);
        verify(personRepository).findById(TEST_PERSON_ID);
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void testDelete() {
        // Act
        todoService.delete(TEST_TODO_ID);

        // Assert
        verify(todoRepository).deleteById(TEST_TODO_ID);
    }

    @Test
    void testFindByPersonId() {
        // Arrange
        when(todoRepository.findByPerson_Id(TEST_PERSON_ID)).thenReturn(List.of(todo));

        // Act
        List<TodoDto> result = todoService.findByPersonId(TEST_PERSON_ID);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(TEST_TITLE, result.get(0).title());
        verify(todoRepository).findByPerson_Id(TEST_PERSON_ID);
    }

    @Test
    void testFindByCompleted() {
        // Arrange
        when(todoRepository.findByCompleted(false)).thenReturn(List.of(todo));

        // Act
        List<TodoDto> result = todoService.findByCompleted(false);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertFalse(result.get(0).completed());
        verify(todoRepository).findByCompleted(false);
    }


}