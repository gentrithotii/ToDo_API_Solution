
package se.lexicon.todo_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.lexicon.todo_app.dto.TodoDto;
import se.lexicon.todo_app.service.TodoService;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@Validated
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Todo API", description = "API endpoints for managing todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all todos", description = "Retrieves a list of all todo items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved todo list")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getAllTodos() {
        return todoService.findAll();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get todo by ID", description = "Retrieves a specific todo item by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved todo"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TodoDto getTodoById(
            @Parameter(description = "ID of the todo to retrieve")
            @PathVariable("id")
            @NotNull(message = "Id cannot be null")
            @Positive(message = "Id must be positive")
            Long id) {
        return todoService.findById(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create new todo", description = "Creates a new todo item")
    @ApiResponse(responseCode = "201", description = "Todo successfully created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoDto createTodo(
            @Parameter(description = "Todo details")
            @RequestBody @Valid TodoDto todoDto) {
        return todoService.create(todoDto);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update todo", description = "Updates an existing todo item")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo successfully updated"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTodo(
            @Parameter(description = "ID of the todo to update")
            @PathVariable @NotNull(message = "Id cannot be null") Long id,
            @Parameter(description = "Updated todo details")
            @RequestBody @Valid TodoDto todoDto) {
        todoService.update(id, todoDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete todo", description = "Deletes a todo item")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @Parameter(description = "ID of the todo to delete")
            @PathVariable @NotNull(message = "Id cannot be null") Long id) {
        todoService.delete(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get todos by person", description = "Retrieves all todos for a specific person")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved todos")
    @GetMapping("/person/{personId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getTodosByPerson(
            @Parameter(description = "ID of the person")
            @PathVariable @NotNull(message = "Person id cannot be null") Long personId) {
        return todoService.findByPersonId(personId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get todos by status", description = "Retrieves todos based on completion status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved todos")
    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getTodosByStatus(
            @Parameter(description = "Completion status of todos")
            @RequestParam(required = true) boolean completed) {
        return todoService.findByCompleted(completed);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get overdue todos", description = "Retrieves all overdue todo items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved overdue todos")
    @GetMapping("/overdue")
    @ResponseStatus(HttpStatus.OK)
    public List<TodoDto> getOverdueTodos() {
        return todoService.findOverdueTodos();
    }
}