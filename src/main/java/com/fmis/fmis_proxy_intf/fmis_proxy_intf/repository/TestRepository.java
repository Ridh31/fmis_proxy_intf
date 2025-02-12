package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Test} entities.
 */
@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {

    /**
     * Finds a test entity by its name.
     *
     * @param name The name of the test.
     * @return The test entity.
     */
    Test findByName(String name);

    /**
     * Retrieves all test entities, sorted in ascending order by name.
     *
     * @return A list of test entities sorted by name.
     */
    List<Test> findAllByOrderByNameAsc();
}
