package com.rlabs.crm.repository;

import com.rlabs.crm.domain.CustomerAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAuditHistoryRepository extends JpaRepository<CustomerAuditHistory, Long> {
    List<CustomerAuditHistory> findByCustomerIdOrderByIdDesc(Long customerId);
}
