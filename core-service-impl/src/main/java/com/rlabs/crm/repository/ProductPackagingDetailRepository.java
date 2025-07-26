package com.rlabs.crm.repository;

import com.rlabs.crm.domain.ProductPackagingDetail;
import com.rlabs.crm.repository.esrepository.ICustomElasticSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPackagingDetailRepository extends ICustomElasticSearchRepository<ProductPackagingDetail, Long> {

}
