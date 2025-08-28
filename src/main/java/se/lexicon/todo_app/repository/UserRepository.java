package se.lexicon.todo_app.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import se.lexicon.todo_app.entity.Role;
import se.lexicon.todo_app.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    List<User> findByRolesContaining(Role role);

    @Modifying
    @Query("update User u set u.expired = :status where u.username = :username")
    void updateExpiredByUsername(@Param("username") String username, @Param("status") boolean status);

    @Modifying
    @Query("update User u set u.password = :pwd where u.username = :username")
    void updatePasswordByUsername(@Param("username") String username, @Param("pwd") String newPassword);
}