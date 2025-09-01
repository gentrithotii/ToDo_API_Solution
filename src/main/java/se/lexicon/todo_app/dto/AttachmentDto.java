package se.lexicon.todo_app.dto;

public record AttachmentDto(
    Long id,
    String fileName,
    String fileType,
    byte[] data
) {}