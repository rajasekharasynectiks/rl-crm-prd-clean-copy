package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Quotation;
import com.rlabs.crm.repository.esrepository.ICustomElasticSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends ICustomElasticSearchRepository<Quotation, Long> {

}
