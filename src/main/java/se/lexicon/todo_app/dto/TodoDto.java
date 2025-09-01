package se.lexicon.todo_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TodoDto(
        Long id,

        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
        String title,

        @Size(max = 500, message = "Description must be less than 500 characters")
        String description,

        boolean completed,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        LocalDateTime dueDate,

        Long personId,

        int numberOfAttachments,

        List<AttachmentDto> attachments

) {

        public TodoDto withAttachments(List<AttachmentDto> newAttachments) {
                return new TodoDto(
                        id, title, description, completed,
                        createdAt, updatedAt, dueDate, personId,
                        newAttachments != null ? newAttachments.size() : 0,
                        newAttachments
                );
        }


}


