package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import java.util.List;

/**
 * Service interface for managing Test entities.
 */
public interface TestService {

    /**
     * Retrieves a Test entity by its name.
     *
     * @param name The name of the test.
     * @return The Test entity if found, otherwise null.
     */
    Test test(String name);

    /**
     * Retrieves all Test entities.
     *
     * @return A list of all Test entities.
     */
    List<Test> getAllTests();

    /**
     * Saves a new Test entity to the repository.
     *
     * @param test The Test entity to be added.
     */
    void addTest(Test test);
}
