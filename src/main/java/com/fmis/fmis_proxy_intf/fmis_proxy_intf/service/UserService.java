package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import java.util.Optional;

/**
 * Service interface for handling user-related operations.
 */
public interface UserService {

    /**
     * Registers a new user by saving their information to the database.
     *
     * @param user the user to be registered
     * @return the registered user
     */
    User registerUser(User user);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user to find
     * @return an Optional containing the user, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user to find
     * @return an Optional containing the user, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find
     * @return an Optional containing the user, or empty if not found
     */
    Optional<User> findById(Long id);

    /**
     * Saves or updates the user.
     *
     * @param user the user to be saved or updated
     * @return the saved or updated user
     */
    User save(User user);

    /**
     * Finds a user by their partner ID and username.
     *
     * @param partnerId the partner ID associated with the user
     * @param username the username of the user to find
     * @return an Optional containing the user, or empty if not found
     */
    Optional<User> findByPartnerIdAndUsername(Long partnerId, String username);
}
