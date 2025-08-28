package se.lexicon.todo_app.service;

import se.lexicon.todo_app.dto.AuthRequestDto;
import se.lexicon.todo_app.dto.AuthResponseDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto request);

    void logout(String authHeader);
}