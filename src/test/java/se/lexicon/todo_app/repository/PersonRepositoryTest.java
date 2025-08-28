package se.lexicon.todo_app.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.lexicon.todo_app.entity.Person;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    @DisplayName("Save and find by email")
    void testSaveAndFindByEmail() {
        // Arrange
        Person person = new Person("John Doe", "john.doe@example.com");
        personRepository.save(person);

        // Act
        var retrievedPerson = personRepository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(retrievedPerson.isPresent());
        assertEquals("John Doe", retrievedPerson.get().getName());
        assertEquals("john.doe@example.com", retrievedPerson.get().getEmail());
    }

    @Test
    @DisplayName("Exists by email should return true")
    void testExistsByEmail() {
        // Arrange
        Person person = new Person("Jane Doe", "jane.doe@example.com");
        personRepository.save(person);

        // Act
        boolean exists = personRepository.existsByEmail("jane.doe@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists by email should return false for unknown email")
    void testExistsByEmailFalse() {
        // Act
        boolean exists = personRepository.existsByEmail("unknown@example.com");

        // Assert
        assertFalse(exists);
    }


    @Test
    @DisplayName("Update a person's name and verify the change")
    void testUpdatePersonName() {
        // Arrange
        Person person = new Person("Alice", "alice@example.com");
        person = personRepository.save(person);
        person.setName("Alice Smith");

        // Act
        personRepository.save(person);
        var updatedPerson = personRepository.findByEmail("alice@example.com");

        // Assert
        assertTrue(updatedPerson.isPresent());
        assertEquals("Alice Smith", updatedPerson.get().getName());
    }

    @Test
    @DisplayName("Delete a person by email and verify existence")
    void testDeletePersonByEmail() {
        // Arrange
        Person person = new Person("Bob", "bob@example.com");
        personRepository.save(person);

        // Act
        personRepository.delete(person);
        boolean exists = personRepository.existsByEmail("bob@example.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Retrieve all persons from the repository")
    void testFindAllPersons() {
        // Arrange
        Person person1 = new Person("Charlie", "charlie@example.com");
        Person person2 = new Person("Dana", "dana@example.com");
        personRepository.save(person1);
        personRepository.save(person2);

        // Act
        var allPersons = personRepository.findAll();

        // Assert
        assertEquals(2, allPersons.size());
    }
}
