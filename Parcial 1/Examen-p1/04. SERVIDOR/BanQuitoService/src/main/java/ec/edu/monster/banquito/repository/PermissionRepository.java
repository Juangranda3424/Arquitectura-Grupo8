package ec.edu.monster.banquito.repository;

import ec.edu.monster.banquito.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {}
