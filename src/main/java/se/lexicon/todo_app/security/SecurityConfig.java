package se.lexicon.todo_app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * SecurityConfig is the main configuration class for Spring Security in the To-Do application.
 * It sets up the security filter chain, configures HTTP security, and defines the authentication manager.
 * It also registers the JwtRequestFilter to intercept requests and validate JWT tokens.
 * This configuration allows for stateless session management, meaning that the server does not store any session information.
 * It also enables method-level security annotations such as @Secured, @PreAuthorize, and @PostAuthorize.
 * It allows public access to authentication endpoints and Swagger UI, while securing all other endpoints.
 * It uses BCrypt for password encoding to enhance security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)

public class SecurityConfig {


    private final JwtRequestFilter jwtRequestFilter;
    private final CorsFilter corsFilter;

    @Autowired
    public SecurityConfig(JwtRequestFilter jwtRequestFilter, CorsFilter corsFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.corsFilter = corsFilter;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Configure URL-based security rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()           // Allow public access to auth endpoints
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll() // Allow access to API documentation
                        .anyRequest().authenticated()                          // Require authentication for all other requests
                )
                // Configure session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Don't create sessions - use JWT instead
                )

                // Add security filters in specific order
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)         // Process CORS before authentication
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Process JWT before authentication

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(true);
        return new ProviderManager(provider);
    }
}