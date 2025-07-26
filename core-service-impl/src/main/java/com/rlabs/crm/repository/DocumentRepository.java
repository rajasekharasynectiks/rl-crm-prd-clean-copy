package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Document;
import com.rlabs.crm.repository.esrepository.ICustomElasticSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends ICustomElasticSearchRepository<Document, Long> {

}
