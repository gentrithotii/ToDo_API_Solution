
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.lexicon.todo_app.dto.AttachmentDto;
import se.lexicon.todo_app.dto.TodoDto;
import se.lexicon.todo_app.service.TodoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    @PostMapping(
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TodoDto createTodo(
            @Parameter(description = "Todo details")
            @RequestPart("todo") @Valid TodoDto todoDto,
            @Parameter(description = "File attachments (max 5 files, 2MB each)")
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        validateFiles(files);
        List<AttachmentDto> attachments = convertFilesToAttachments(files);
        todoDto = todoDto.withAttachments(attachments);
        System.out.println("todoDto = " + todoDto);
        return todoService.create(todoDto);
    }


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update todo", description = "Updates an existing todo item with optional attachments (max 5 files, 2MB each)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo successfully updated"),
            @ApiResponse(responseCode = "404", description = "Todo not found")
    })
    @PutMapping(
            value = "/{id}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public TodoDto updateTodo(
            @Parameter(description = "ID of the todo to update")
            @PathVariable @NotNull(message = "Id cannot be null") Long id,
            @Parameter(description = "Updated todo details")
            @RequestPart("todo") @Valid TodoDto todoDto,
            @Parameter(description = "File attachments (max 5 files, 2MB each)")
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        validateFiles(files);
        List<AttachmentDto> attachments = convertFilesToAttachments(files);
        todoDto = todoDto.withAttachments(attachments);
        return todoService.update(id, todoDto);
    }




    private void validateFiles(MultipartFile[] files) {
        if (files == null) return;

        if (files.length > 5) {
            throw new IllegalArgumentException("Maximum 5 files allowed");
        }


        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Empty file not allowed: " + file.getOriginalFilename());
            }

            if (file.getSize() > 2 * 1024 * 1024) { // 2MB in bytes
                throw new IllegalArgumentException("File size exceeds 2MB limit: " + file.getOriginalFilename());
            }

            System.out.println("file = " + file);
        }
    }


    private List<AttachmentDto> convertFilesToAttachments(MultipartFile[] files) {
        if (files == null) return new ArrayList<>();

        List<AttachmentDto> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                attachments.add(new AttachmentDto(
                        null,
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                ));
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file: " + file.getOriginalFilename(), e);
            }
        }
        return attachments;
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