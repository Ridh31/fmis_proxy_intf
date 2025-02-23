package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by its username.
     *
     * @param username the username of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a User by its ID.
     *
     * @param id the ID of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findById(Long id);

    /**
     * Finds a User by its partner ID and username.
     *
     * @param partnerId the partner ID associated with the user
     * @param username the username of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByPartnerIdAndUsername(Long partnerId, String username);
}
