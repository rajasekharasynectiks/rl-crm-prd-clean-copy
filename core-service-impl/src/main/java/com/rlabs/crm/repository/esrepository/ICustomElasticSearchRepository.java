package com.rlabs.crm.repository.esrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Interface to extend JPA functionality to index entities into elastic search
 * @author Manoj Sharma
 */
@NoRepositoryBean
public interface ICustomElasticSearchRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

}
