package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SecurityServer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for managing {@link SecurityServer} entities.
 * Provides CRUD operations and custom query methods.
 */
public interface SecurityServerRepository extends JpaRepository<SecurityServer, Long> {

    /**
     * Retrieves a SecurityServer by its unique configuration key.
     *
     * @param configKey the unique configuration key
     * @return an {@link Optional} containing the found SecurityServer, or empty if not found
     */
    Optional<SecurityServer> findByConfigKey(String configKey);

    /**
     * Checks whether a SecurityServer with the given name exists.
     *
     * @param name the name of the SecurityServer
     * @return true if a matching SecurityServer exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks whether a SecurityServer with the given configKey exists.
     *
     * @param configKey the unique configuration key
     * @return true if a matching SecurityServer exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);

    /**
     * Retrieves a paginated list of active and non-deleted {@link SecurityServer} records,
     * filtered optionally by name, config key, description, and creation date.
     *
     * The query checks for `status` being true and `is_deleted` being false.
     * Additionally, each filter is applied only if the corresponding parameter is not null.
     *
     * @param name        optional filter by name
     * @param configKey   optional filter by config key
     * @param description optional filter by description
     * @param createdDate optional filter by creation date in "dd-MM-yyyy" format
     * @param pageable    the pagination details (page number, page size)
     * @return a {@link Page} of filtered {@link SecurityServer} entities matching the criteria
     */
    @Query(value = """
        SELECT
            *
        FROM
            security_server ss
        WHERE
            ss.status = TRUE
            AND ss.is_deleted = FALSE
            AND (:name IS NULL OR ss.name LIKE CONCAT('%', :name, '%'))
            AND (:configKey IS NULL OR ss.config_key LIKE CONCAT('%', :configKey, '%'))
            AND (:description IS NULL OR ss.description LIKE CONCAT('%', :description, '%'))
            AND (:createdDate IS NULL OR ss.created_date >= :createdDate
                AND ss.created_date < DATE_ADD(:createdDate, INTERVAL 1 DAY))
        ORDER BY ss.id DESC
    """, nativeQuery = true)
    Page<SecurityServer> findFilteredSecurityServers(
            @Param("name") String name,
            @Param("configKey") String configKey,
            @Param("description") String description,
            @Param("createdDate") LocalDate createdDate,
            Pageable pageable
    );
}