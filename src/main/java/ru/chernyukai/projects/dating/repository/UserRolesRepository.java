package ru.chernyukai.projects.dating.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.chernyukai.projects.dating.model.UserRole;

public interface UserRolesRepository extends CrudRepository<UserRole, Long> {

    @Query(value = "insert into user_roles (user_authority, user_id) values (1, 11)", nativeQuery = true)
    void makeAdmib();
}
