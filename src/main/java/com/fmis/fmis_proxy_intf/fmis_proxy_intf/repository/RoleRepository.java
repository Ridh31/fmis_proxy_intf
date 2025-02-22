package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;

import java.util.Optional;

/**
 * Repository interface for accessing Role entities in the database.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find a Role by its ID.
     *
     * @param id the ID of the Role to be retrieved
     * @return an Optional containing the Role, or empty if not found
     */
    Optional<Role> findById(Long id);

    /**
     * Check if a Role exists by its ID.
     *
     * @param id the ID of the Role to check
     * @return true if the Role exists, false otherwise
     */
    boolean existsById(Long id);
}
