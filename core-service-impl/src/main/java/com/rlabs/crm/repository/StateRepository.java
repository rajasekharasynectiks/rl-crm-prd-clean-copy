package com.rlabs.crm.repository;

import com.rlabs.crm.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    Optional<State> findByStateCode(String stateCode);
    Optional<State> findByStateName(String stateName);
    List<State> findByCountryId(Long countryId);

}
