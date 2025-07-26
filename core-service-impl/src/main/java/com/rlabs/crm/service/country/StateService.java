package com.rlabs.crm.service.country;

import com.rlabs.crm.domain.State;
import com.rlabs.crm.repository.StateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    public List<State> findAll(){
        return stateRepository.findAll();
    }

    public State findById(Long id){
        return stateRepository.findById(id).orElse(null);
    }

    public State findByStateCode(String code) {
        return stateRepository.findByStateCode(code).orElse(null);
    }

    public State findByStateName(String name) {
        return stateRepository.findByStateName(name).orElse(null);
    }

    public List<State> findByCountryId(Long countryId) {
        return stateRepository.findByCountryId(countryId);
    }


}
