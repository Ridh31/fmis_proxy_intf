package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.RoleRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.RoleService;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service implementation for managing Role entities.
 * Implements the RoleService interface to provide business logic for Role management.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * Constructor-based dependency injection for initializing the RoleRepository.
     *
     * @param roleRepository the RoleRepository to interact with the database
     */
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Find a Role by its ID.
     *
     * @param id the ID of the Role to be retrieved
     * @return an Optional containing the Role, or empty if not found
     */
    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    /**
     * Check if a Role exists by its ID.
     *
     * @param id the ID of the Role to check
     * @return true if the Role exists, false otherwise
     */
    @Override
    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }
}
