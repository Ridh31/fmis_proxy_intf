package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
