package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Permission;
import com.rlabs.crm.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
  List<Permission> findByName(String name);
  List<Permission> findByCategory(String category);
}
