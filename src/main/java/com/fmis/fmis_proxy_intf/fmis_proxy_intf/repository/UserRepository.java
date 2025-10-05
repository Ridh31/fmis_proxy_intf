package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for performing CRUD and custom queries on {@link User} entities.
 * Extends {@link JpaRepository} to inherit standard database operations.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username of the user
     * @return an {@link Optional} containing the user if found, otherwise empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the user if found, otherwise empty
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their unique ID.
     *
     * @param id the ID of the user
     * @return an {@link Optional} containing the user if found, otherwise empty
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by their partner ID and username.
     *
     * @param partnerId the partner ID associated with the user
     * @param username  the username of the user
     * @return an {@link Optional} containing the user if found, otherwise empty
     */
    Optional<User> findByPartnerIdAndUsername(Long partnerId, String username);

    /**
     * Checks if a user exists in the database based on their unique username.
     *
     * @param username the username of the user to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Retrieves all enabled users in descending order of ID.
     *
     * @param username optional filter by username
     * @param pageable pagination and sorting information
     * @return a {@link Page} of enabled users
     */
    @Query(value = """
        SELECT
            *
        FROM
            user u
        WHERE
            u.enabled = TRUE
            AND (:username IS NULL OR LOWER(u.username) LIKE CONCAT('%', LOWER(:username), '%'))
        ORDER BY u.id DESC
        """, nativeQuery = true)
    Page<User> getAllUsers(@Param("username") String username, Pageable pageable);
}