package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.TestRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.TestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;

    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public Test test(String name) {
        return testRepository.findByName(name);
    }

    @Override
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    @Override
    public void addTest(Test test) {
        testRepository.save(test);
    }
}
