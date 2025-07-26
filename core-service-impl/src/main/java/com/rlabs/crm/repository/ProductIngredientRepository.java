package com.rlabs.crm.repository;

import com.rlabs.crm.domain.ProductIngredient;
import com.rlabs.crm.repository.esrepository.ICustomElasticSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductIngredientRepository extends ICustomElasticSearchRepository<ProductIngredient, Long> {

}
