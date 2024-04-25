package ru.chernyukai.projects.dating.repository;

import org.springframework.data.repository.CrudRepository;
import ru.chernyukai.projects.dating.model.UserRole;

public interface UserRolesRepository extends CrudRepository<UserRole, Long> {
}
