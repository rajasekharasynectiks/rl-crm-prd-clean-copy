package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByIsoTwoCharCode(String isoTwoCharCode);
    Optional<Country> findByIsoThreeCharCode(String isoThreeCharCode);
    Optional<Country> findByName(String name);
    Optional<Country> findByCurrencyCode(String currencyCode);

}
