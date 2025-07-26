package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Boolean existsByName(String name);

    String USERS_SHARED_ROLE_QUERY ="select u.id, u.username, u.first_name, u.last_name from users_roles ur, users u where ur.user_id  = u.id and ur.role_id = :roleId ";
    @Query(value = USERS_SHARED_ROLE_QUERY, nativeQuery = true)
    Object[] getUsersByRole(@Param("roleId") Long roleId);

    String DELETE_ROLES_PERMISSIONS_QUERY = "delete from roles_permissions where role_id = :roleId";
    @Modifying
    @Query(value = DELETE_ROLES_PERMISSIONS_QUERY, nativeQuery = true)
    void deleteRolesPermissions(@Param("roleId") Long roleId);

    String DELETE_USERS_ROLES_QUERY = "delete from users_roles where role_id = :roleId";
    @Modifying
    @Query(value = DELETE_USERS_ROLES_QUERY, nativeQuery = true)
    void deleteUsersRoles(@Param("roleId") Long roleId);
}
