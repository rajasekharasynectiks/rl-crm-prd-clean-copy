package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.repository.esrepository.ICustomElasticSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ICustomElasticSearchRepository<Customer, Long> {

}
