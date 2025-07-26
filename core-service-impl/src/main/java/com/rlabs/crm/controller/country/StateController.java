package com.rlabs.crm.controller.country;

import com.rlabs.crm.api.controller.StateApi;
import com.rlabs.crm.payload.response.state.StateResponse;
import com.rlabs.crm.service.country.StateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class StateController implements StateApi {

    @Autowired
    private StateService stateService;

    @Override
    public ResponseEntity<Object> getByStateCode(String code) {
        log.info("Request to get state by state code. Code: {}",code);
        return ResponseEntity.status(HttpStatus.OK).body(StateResponse.buildResponse(stateService.findByStateCode(code)));
    }

    @Override
    public ResponseEntity<Object> getByName(String name) {
        log.info("Request to get state by name. Name: {}",name);
        return ResponseEntity.status(HttpStatus.OK).body(StateResponse.buildResponse(stateService.findByStateName(name)));
    }

    @Override
    public ResponseEntity<Object> getByCountryId(Long countryId) {
        log.info("Request to get list of states by country id. Country id: {}",countryId);
        List<StateResponse> stateResponseList = stateService.findByCountryId(countryId).stream().map(StateResponse::buildResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(stateResponseList);
    }
}
