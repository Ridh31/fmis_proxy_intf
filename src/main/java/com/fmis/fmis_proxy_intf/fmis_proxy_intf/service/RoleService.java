package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;
import java.util.Optional;

/**
 * Service interface for managing Role entities.
 * Defines the contract for Role-related operations.
 */
public interface RoleService {

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
