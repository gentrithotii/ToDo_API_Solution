package se.lexicon.todo_app.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.lexicon.todo_app.entity.Person;
import se.lexicon.todo_app.entity.Todo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private PersonRepository personRepository;

    private Person testPerson;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // Create a test person
        testPerson = new Person();
        testPerson.setName("Test Person");
        testPerson.setEmail("test@example.com");
        testPerson = personRepository.save(testPerson);

        now = LocalDateTime.now();

        // Create some sample todos
        Todo todo1 = new Todo("Shopping", "Buy groceries", false, now.plusDays(1));
        todo1.setPerson(testPerson);

        Todo todo2 = new Todo("Study", "Learn Spring Boot", true, now.plusDays(2));
        todo2.setPerson(testPerson);

        Todo todo3 = new Todo("Gym", "Workout session", false);

        Todo todo4 = new Todo("Reading", "Read chapter 5", false, now.minusDays(1));
        todo4.setPerson(testPerson);

        todoRepository.saveAll(List.of(todo1, todo2, todo3, todo4));
    }

    @Test
    @DisplayName("Find Todos containing case-insensitive title substring should return matching Todos")
    void findByTitleContainingIgnoreCase_ShouldReturnMatchingTodos() {
        List<Todo> result = todoRepository.findByTitleContainingIgnoreCase("ing");
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(todo -> todo.getTitle().equals("Shopping")));
        assertTrue(result.stream().anyMatch(todo -> todo.getTitle().equals("Reading")));
    }

    @Test
    @DisplayName("Find Todos by Person ID should return that person's Todos")
    void findByPerson_Id_ShouldReturnPersonsTodos() {
        List<Todo> result = todoRepository.findByPerson_Id(testPerson.getId());
        assertEquals(3, result.size());
        result.forEach(todo -> assertEquals(testPerson.getId(), todo.getPerson().getId()));
    }

    @Test
    @DisplayName("Find Todos by completion status should return completed Todos")
    void findByCompleted_ShouldReturnCompletedTodos() {
        List<Todo> completedTodos = todoRepository.findByCompleted(true);
        assertEquals(1, completedTodos.size());
        assertTrue(completedTodos.get(0).isCompleted());
    }

    @Test
    @DisplayName("Find Todos due within a date range should return matching Todos")
    void findByDueDateBetween_ShouldReturnTodosInDateRange() {
        // Setup fixed datetime for testing
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0);

        // Create test data
        Todo todo1 = new Todo("Task 1", "Description 1", false);
        todo1.setDueDate(baseTime.minusHours(12));

        Todo todo2 = new Todo("Task 2", "Description 2", false);
        todo2.setDueDate(baseTime.plusHours(12));

        Todo todo3 = new Todo("Task 3", "Description 3", false);
        todo3.setDueDate(baseTime.plusDays(1));

        // Save test data
        todoRepository.saveAll(Arrays.asList(todo1, todo2, todo3));

        // Execute test
        List<Todo> result = todoRepository.findByDueDateBetween(
                baseTime.minusDays(1),
                baseTime.plusDays(2)
        );

        // Verify
        assertEquals(3, result.size());
        assertTrue(result.stream()
                .allMatch(todo ->
                        todo.getDueDate().isAfter(baseTime.minusDays(1)) &&
                                todo.getDueDate().isBefore(baseTime.plusDays(2))
                ));
    }

    @Test
    @DisplayName("Find overdue and incomplete Todos should return matching Todos")
    void findByDueDateBeforeAndCompletedFalse_ShouldReturnOverdueTodos() {
        List<Todo> overdueTodos = todoRepository.findByDueDateBeforeAndCompletedFalse(now);
        assertEquals(1, overdueTodos.size());
        assertFalse(overdueTodos.get(0).isCompleted());
        assertTrue(overdueTodos.get(0).getDueDate().isBefore(now));
    }

    @Test
    @DisplayName("Find unassigned Todos should return Todos with no Person set")
    void findByPersonIsNull_ShouldReturnUnassignedTodos() {
        List<Todo> unassignedTodos = todoRepository.findByPersonIsNull();
        assertEquals(1, unassignedTodos.size());
        assertNull(unassignedTodos.get(0).getPerson());
    }

    @Test
    @DisplayName("Find unfinished overdue Todos should return matching Todos")
    void findByCompletedFalseAndDueDateBefore_ShouldReturnUnfinishedOverdueTasks() {
        List<Todo> unfinishedOverdueTasks = todoRepository.findByCompletedFalseAndDueDateBefore(now);
        assertEquals(1, unfinishedOverdueTasks.size());
        Todo overdueTodo = unfinishedOverdueTasks.get(0);
        assertFalse(overdueTodo.isCompleted());
        assertTrue(overdueTodo.getDueDate().isBefore(now));
    }

    @Test
    @DisplayName("Find completed Todos for a Person by ID should return matching Todos")
    void findByPersonIdAndCompletedTrue_ShouldReturnCompletedTasksForPerson() {
        List<Todo> completedPersonTasks = todoRepository.findByPersonIdAndCompletedTrue(testPerson.getId());
        assertEquals(1, completedPersonTasks.size());
        Todo completedTodo = completedPersonTasks.get(0);
        assertTrue(completedTodo.isCompleted());
        assertEquals(testPerson.getId(), completedTodo.getPerson().getId());
    }

    @Test
    @DisplayName("Find Todos with no due date should return matching Todos")
    void findByDueDateIsNull_ShouldReturnTodosWithNoDueDate() {
        Todo todoWithoutDueDate = new Todo("No deadline task", "Description", false);
        todoRepository.save(todoWithoutDueDate);

        List<Todo> todosWithNoDueDate = todoRepository.findByDueDateIsNull();
        assertEquals(2, todosWithNoDueDate.size());  // Changed from 1 to 2
        assertTrue(todosWithNoDueDate.stream().allMatch(todo -> todo.getDueDate() == null));
    }


    @Test
    @DisplayName("Count Todos for a Person by ID should return correct count")
    void countByPersonId_ShouldReturnCorrectCount() {
        long todoCount = todoRepository.countByPersonId(testPerson.getId());
        assertEquals(3, todoCount);
    }

    @Test
    @DisplayName("Save Todo should persist the Todo and return it with generated ID")
    void saveTodo_ShouldPersistTodo() {
        Todo newTodo = new Todo("New Task", "Description", false, now.plusDays(3));
        Todo saved = todoRepository.save(newTodo);

        assertNotNull(saved.getId());
        assertEquals("New Task", saved.getTitle());
        assertEquals("Description", saved.getDescription());
        assertFalse(saved.isCompleted());
        assertEquals(now.plusDays(3).withNano(0), saved.getDueDate().withNano(0));
    }
}