package se.lexicon.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.todo_app.entity.Todo;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 🔍 Find todos by title keyword (case-insensitive contains)
    List<Todo> findByTitleContainingIgnoreCase(String title);
    // SELECT * FROM todos WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'));

    // 👤 Find todos by person ID
    List<Todo> findByPerson_Id(Long personId);
    // SELECT * FROM todos WHERE person_id = :personId;

    // ✅ Find todos by completed status
    List<Todo> findByCompleted(boolean completed);
    // SELECT * FROM todos WHERE completed = :completed;

    // 🗓️ Find todos between two due dates
    List<Todo> findByDueDateBetween(LocalDateTime start, LocalDateTime end);
    // SELECT * FROM todos WHERE due_date BETWEEN :start AND :end;

    // 🗓️ Find todos due before a specific date and not completed
    List<Todo> findByDueDateBeforeAndCompletedFalse(LocalDateTime dateTime);

    // ❌ Find unassigned todos (person is null)
    List<Todo> findByPersonIsNull();

    // 🔥 Find unfinished & overdue tasks (custom query)
    List<Todo> findByCompletedFalseAndDueDateBefore(LocalDateTime dateTime);

    // ✅ Find completed tasks assigned to a specific person
    List<Todo> findByPersonIdAndCompletedTrue(Long personId);

    // 📅 Find all with no due date
    List<Todo> findByDueDateIsNull();

    // 📌 Count all tasks assigned to a person
    long countByPersonId(Long personId);
}
