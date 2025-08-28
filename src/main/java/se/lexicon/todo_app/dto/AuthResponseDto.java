package se.lexicon.todo_app.dto;

import lombok.Builder;

@Builder
public record AuthResponseDto(
        String token,
        String type,
        String username,
        String name,
        String email,
        String[] roles
) {}