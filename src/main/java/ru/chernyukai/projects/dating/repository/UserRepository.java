package ru.chernyukai.projects.dating.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import ru.chernyukai.projects.dating.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}