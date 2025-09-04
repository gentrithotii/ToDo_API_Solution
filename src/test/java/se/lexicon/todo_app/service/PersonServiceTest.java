package se.lexicon.todo_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
//import se.lexicon.notify.model.Email;
//import se.lexicon.notify.service.MessageService;
import se.lexicon.todo_app.dto.PersonDto;
import se.lexicon.todo_app.dto.PersonRegistrationDto;
import se.lexicon.todo_app.entity.Person;
import se.lexicon.todo_app.entity.Role;
import se.lexicon.todo_app.repository.PersonRepository;
import se.lexicon.todo_app.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {


    @Mock
    private PersonRepository personRepository;

    @Mock
    private UserRepository userRepository;

    //@Mock
    //private MessageService<Email> emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonServiceImpl personService;

    private Person person;
    private PersonDto personDto;
    private final Long TEST_ID = 1L;
    private final String TEST_NAME = "Mehrdad Javan";
    private final String TEST_EMAIL = "mehrdad.javan@lexicon.se";
    private final String TEST_USERNAME = "mehrdad";
    private final String TEST_PASSWORD = "Test123!@#";
    private final Set<Role> TEST_ROLE = new HashSet<>(Arrays.asList(Role.ADMIN, Role.USER));

    @BeforeEach
    void setUp() {
        person = new Person(TEST_NAME, TEST_EMAIL);
        person.setId(TEST_ID);
        personDto = new PersonDto(TEST_ID, TEST_NAME, TEST_EMAIL);
    }

    @Test
    void testCreate() {
        // Arrange
        PersonRegistrationDto registrationDto = new PersonRegistrationDto(
                TEST_NAME,
                TEST_EMAIL,
                TEST_USERNAME,
                TEST_PASSWORD,
                TEST_PASSWORD,
                TEST_ROLE.toString()
        );

        Person savedPerson = new Person(TEST_NAME, TEST_EMAIL);
        savedPerson.setId(TEST_ID);

        when(personRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        // Act
        PersonDto created = personService.create(registrationDto);

        // Assert
        assertNotNull(created);
        assertEquals(TEST_NAME, created.name());
        assertEquals(TEST_EMAIL, created.email());
        verify(personRepository).save(any(Person.class));
        // Removed verify(userRepository).save(any()) since it's handled by cascade
    }

    @Test
    void testFindAll() {
        // Arrange
        Person person2 = new Person("John Doe", "john.doe@lexicon.se");
        person2.setId(2L);
        when(personRepository.findAll()).thenReturn(Arrays.asList(person, person2));

        // Act
        List<PersonDto> result = personService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TEST_NAME, result.get(0).name());
        assertEquals("John Doe", result.get(1).name());
        verify(personRepository).findAll();
    }

    @Test
    void testUpdate() {
        // Arrange
        String updatedName = "Mehrdad Updated";
        String updatedEmail = "mehrdad.lexicon.updated@lexicon.se";
        PersonDto updateDto = new PersonDto(TEST_ID, updatedName, updatedEmail);

        Person existingPerson = new Person(TEST_NAME, TEST_EMAIL);
        existingPerson.setId(TEST_ID);

        Person updatedPerson = new Person(updatedName, updatedEmail);
        updatedPerson.setId(TEST_ID);

        when(personRepository.findById(TEST_ID)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);

        // Act
        PersonDto result = personService.update(TEST_ID, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updatedName, result.name());
        assertEquals(updatedEmail, result.email());
        verify(personRepository).findById(TEST_ID);
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void testDelete() {
        // Act
        personService.delete(TEST_ID);

        // Assert
        verify(personRepository).deleteById(TEST_ID);
    }

    @Test
    void testFindByEmail() {
        // Arrange
        when(personRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(person));

        // Act
        PersonDto found = personService.findByEmail(TEST_EMAIL);

        // Assert
        assertNotNull(found);
        assertEquals(TEST_EMAIL, found.email());
        verify(personRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void testFindByEmail_NotFound() {
        // Arrange
        when(personRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> personService.findByEmail(TEST_EMAIL));
        verify(personRepository).findByEmail(TEST_EMAIL);
    }
}