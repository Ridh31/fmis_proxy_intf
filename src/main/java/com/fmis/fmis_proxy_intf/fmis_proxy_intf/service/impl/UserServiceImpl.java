package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Role;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.UserRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.UserService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResponseMessageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service implementation for user-related operations and authentication.
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new {@code UserServiceImpl} with the provided dependencies.
     *
     * @param userRepository  the repository for accessing user data
     * @param passwordEncoder the encoder for encrypting passwords
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username if authenticated, otherwise {@code null}
     */
    @Override
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    /**
     * Registers a new user by encoding their password and saving the entity.
     *
     * @param user the user to register
     * @return the saved user entity
     */
    @Override
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Loads user details by username for authentication purposes.
     *
     * @param username the username to search for
     * @return a {@link UserDetails} object representing the user
     * @throws UsernameNotFoundException if the user does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ResponseMessageUtil.notFound("User")));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an {@link Optional} of the user if found
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an {@link Optional} of the user if found
     */
    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param id the user ID
     * @return an {@link Optional} of the user if found
     */
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Persists a user entity to the database.
     *
     * @param user the user to save
     * @return the saved user entity
     */
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Finds a user by partner ID and username.
     *
     * @param partnerId the partner ID
     * @param username  the username
     * @return an {@link Optional} of the user if found
     */
    @Override
    public Optional<User> findByPartnerIdAndUsername(Long partnerId, String username) {
        return userRepository.findByPartnerIdAndUsername(partnerId, username);
    }

    /**
     * Determines if the given user has administrative privileges.
     * A user is considered an admin if their role level is 1 (Super Admin) or 2 (Admin).
     *
     * @param user the user whose role is to be checked
     * @return true if the user is a Super Admin or Admin; false otherwise
     */
    @Override
    public boolean isAdmin(User user) {
        Role role = user.getRole();
        return role != null && (role.getLevel() == 1 || role.getLevel() == 2 || role.getLevel() == 3);
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param username optional filter by username
     * @param page the page number (0-based)
     * @param size the number of records per page
     * @return a {@link Page} of users
     */
    @Override
    public Page<User> getAllUsers(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.getAllUsers(username, pageable);
    }

    /**
     * Retrieves a list of users by their IDs in a single query.
     *
     * This method is used for batch-fetching users to avoid multiple
     * database queries when mapping entities to DTOs (e.g., importedBy usernames).
     *
     * @param ids a set of user IDs to fetch
     * @return a list of {@link User} entities matching the provided IDs;
     *         returns an empty list if no users are found
     */
    @Override
    public List<User> findAllByIds(Set<Long> ids) {
        return userRepository.findAllById(ids);
    }
}