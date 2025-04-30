package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * Service interface for handling operations related to {@link User} entities.
 * Provides methods for authentication, retrieval, registration, and persistence.
 */
public interface UserService {

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username of the authenticated user, or {@code null} if not authenticated
     */
    String getAuthenticatedUsername();

    /**
     * Registers a new user by encoding their password and saving them to the database.
     *
     * @param user the {@link User} to register
     * @return the saved {@link User} entity
     */
    User registerUser(User user);

    /**
     * Finds a user by their unique username.
     *
     * @param username the username of the user
     * @return an {@link Optional} containing the user if found, or empty if not
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the user if found, or empty if not
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their unique ID.
     *
     * @param id the ID of the user
     * @return an {@link Optional} containing the user if found, or empty if not
     */
    Optional<User> findById(Long id);

    /**
     * Saves or updates the provided user entity in the database.
     *
     * @param user the {@link User} to save or update
     * @return the persisted {@link User}
     */
    User save(User user);

    /**
     * Finds a user by their partner ID and username.
     *
     * @param partnerId the ID of the associated partner
     * @param username  the username of the user
     * @return an {@link Optional} containing the user if found, or empty if not
     */
    Optional<User> findByPartnerIdAndUsername(Long partnerId, String username);

    /**
     * Retrieves a paginated list of all enabled users.
     *
     * @param page the page number (0-based index)
     * @param size the number of items per page
     * @return a {@link Page} of {@link User} entities
     */
    Page<User> getAllUsers(int page, int size);
}