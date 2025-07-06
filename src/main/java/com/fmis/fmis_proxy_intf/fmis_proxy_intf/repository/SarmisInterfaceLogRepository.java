package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterfaceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and manipulating SarmisInterfaceLog data.
 * Extends JpaRepository to provide CRUD operations for the SarmisInterfaceLog entity.
 */
@Repository
public interface SarmisInterfaceLogRepository extends JpaRepository<SarmisInterfaceLog, Long> {
}