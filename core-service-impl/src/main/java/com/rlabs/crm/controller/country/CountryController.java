package com.rlabs.crm.controller.country;

import com.rlabs.crm.api.controller.CountryApi;
import com.rlabs.crm.payload.response.country.CountryResponse;
import com.rlabs.crm.service.country.CountryService;
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
public class CountryController implements CountryApi {

    @Autowired
    private CountryService countryService;

    @Override
    public ResponseEntity<Object> getAllCountries() {
        log.info("Request to get list of countries");
        List<CountryResponse> countryList = countryService.findAll().stream().map(CountryResponse::buildResponse).toList();
        return ResponseEntity.status(HttpStatus.OK).body(countryList);
    }

    @Override
    public ResponseEntity<Object> getByTwoCharCode(String code) {
        log.info("Request to get country by two character code. Code: {}",code);
        return ResponseEntity.status(HttpStatus.OK).body(CountryResponse.buildResponse(countryService.findByIsoTwoCharCode(code)));
    }

    @Override
    public ResponseEntity<Object> getByThreeCharCode(String code) {
        log.info("Request to get country by three character code. Code: {}",code);
        return ResponseEntity.status(HttpStatus.OK).body(CountryResponse.buildResponse(countryService.findByIsoThreeCharCode(code)));
    }
}
