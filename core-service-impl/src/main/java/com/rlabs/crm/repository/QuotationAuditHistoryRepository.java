package com.rlabs.crm.repository;

import com.rlabs.crm.domain.QuotationAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationAuditHistoryRepository extends JpaRepository<QuotationAuditHistory, Long> {
    List<QuotationAuditHistory> findByQuotationIdOrderByIdDesc(Long quotationId);
}
