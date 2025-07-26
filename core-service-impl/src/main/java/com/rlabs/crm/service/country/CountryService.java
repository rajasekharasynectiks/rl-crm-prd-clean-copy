package com.rlabs.crm.service.country;

import com.rlabs.crm.domain.Country;
import com.rlabs.crm.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public List<Country> findAll(){
        return countryRepository.findAll();
    }

    public List<Country> findAllById(List<Long> ids){
        return countryRepository.findAllById(ids);
    }

    public Country findById(Long id){
        return countryRepository.findById(id).orElse(null);
    }

    public Country findByIsoTwoCharCode(String isoTwoCharCode) {
        return countryRepository.findByIsoTwoCharCode(isoTwoCharCode).orElse(null);
    }

    public Country findByIsoThreeCharCode(String isoThreeCharCode) {
        return countryRepository.findByIsoThreeCharCode(isoThreeCharCode).orElse(null);
    }

    public Country findByName(String name) {
        return countryRepository.findByName(name).orElse(null);
    }

    public Country findByCurrencyCode(String currencyCode){
        return countryRepository.findByCurrencyCode(currencyCode).orElse(null);
    }
}
