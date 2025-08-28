package se.lexicon.todo_app.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import se.lexicon.todo_app.dto.AuthRequestDto;
import se.lexicon.todo_app.dto.AuthResponseDto;
import se.lexicon.todo_app.entity.Person;
import se.lexicon.todo_app.entity.User;
import se.lexicon.todo_app.repository.PersonRepository;
import se.lexicon.todo_app.security.JwtTokenUtil;
import se.lexicon.todo_app.security.TokenBlacklistStorage;
import se.lexicon.todo_app.service.AuthService;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenBlacklistStorage tokenBlacklistStorage;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil,
                           TokenBlacklistStorage tokenBlacklistStorage,
                           PersonRepository personRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenBlacklistStorage = tokenBlacklistStorage;
        this.personRepository = personRepository;
    }


    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtTokenUtil.generateToken(userDetails);

        Person person = personRepository.findByUserUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return AuthResponseDto.builder()
                .token(jwt)
                .type("Bearer")
                .username(userDetails.getUsername())
                .name(person.getName())
                .email(person.getEmail())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .build();
    }

    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        try {
            if (tokenBlacklistStorage.isBlacklisted(token)) {
                throw new IllegalArgumentException("Token has already been invalidated");
            }

            Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(token);
            tokenBlacklistStorage.blacklistToken(token, username, expiryDate.toInstant());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token: " + e.getMessage());
        }
    }
}