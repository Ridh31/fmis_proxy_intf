package com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.SarmisInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and manipulating SarmisInterface data.
 * Extends JpaRepository to provide CRUD operations for the SarmisInterface entity.
 */
@Repository
public interface SarmisInterfaceRepository extends JpaRepository<SarmisInterface, Long> {
}