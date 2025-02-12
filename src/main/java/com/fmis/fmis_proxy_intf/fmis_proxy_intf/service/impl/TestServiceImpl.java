package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.TestRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link TestService} interface.
 * Provides service layer operations for managing Test entities.
 */
@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    /**
     * Constructs a new TestServiceImpl with the given repository.
     *
     * @param testRepository The repository for managing Test entities.
     */
    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    /**
     * Retrieves a Test entity by its name.
     *
     * @param name The name of the test.
     * @return The Test entity if found, otherwise null.
     */
    @Override
    public Test test(String name) {
        return testRepository.findByName(name);
    }

    /**
     * Retrieves all Test entities.
     *
     * @return A list of all Test entities.
     */
    @Override
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    /**
     * Saves a new Test entity to the repository.
     *
     * @param test The Test entity to be added.
     */
    @Override
    public void addTest(Test test) {
        testRepository.save(test);
    }
}
