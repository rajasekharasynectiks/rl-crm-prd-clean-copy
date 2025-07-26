package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Product;
import com.rlabs.crm.repository.esrepository.ICustomElasticSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ICustomElasticSearchRepository<Product, Long> {

}
