package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing FMIS data in the database.
 * Extends JpaRepository to provide basic CRUD operations.
 */
@Repository
public interface FmisRepository extends JpaRepository<FMIS, Long> {

    /**
     * Finds an FMIS entity by its ID.
     *
     * @param id The ID of the FMIS entity.
     * @return An Optional containing the FMIS entity if found, or empty if not.
     */
    Optional<FMIS> findById(Long id);

    /**
     * Retrieves the single FMIS configuration in the system (there is always one row).
     *
     * @return The FMIS configuration.
     */
    Optional<FMIS> findFirstBy();

    /**
     * Retrieves a paginated list of FMIS configuration.
     *
     * @param pageable Contains pagination and sorting information.
     * @return A {@link Page} of FMIS configuration entities.
     */
    @Query(value = """
        SELECT
            *
        FROM
            fmis f
        WHERE
            f.status = TRUE
            AND f.is_deleted = FALSE
    """, nativeQuery = true)
    Page<FMIS> getConfig(Pageable pageable);
}