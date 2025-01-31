package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Test;

import java.util.List;

public interface TestService {
    Test test(String name);
    List<Test> getAllTests();
    void addTest(Test test);
}
