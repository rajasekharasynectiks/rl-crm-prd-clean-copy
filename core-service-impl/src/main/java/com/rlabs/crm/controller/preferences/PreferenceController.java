package com.rlabs.crm.controller.preferences;

import com.rlabs.crm.api.controller.PreferenceApi;
import com.rlabs.crm.api.model.SearchPreferenceRequest;
import com.rlabs.crm.domain.Preferences;
import com.rlabs.crm.payload.request.preferences.AddPreferenceRequest;
import com.rlabs.crm.payload.request.preferences.PreferenceRequest;
import com.rlabs.crm.payload.response.preference.PreferenceResponse;
import com.rlabs.crm.repository.PreferencesRepository;
import com.rlabs.crm.service.preference.PreferencesService;
import com.rlabs.crm.util.DateTimeUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.MandatoryFieldMissingException;
import com.rlabs.crm.web.rest.errors.preferences.PreferenceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class PreferenceController implements PreferenceApi {

    @Autowired
    private PreferencesRepository preferencesRepository;

    @Autowired
    private PreferencesService preferencesService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Override
    public ResponseEntity<Object> addPreference(Object body) {
        log.info("Request to create new preference");
        List<PreferenceResponse> preferencesList = new ArrayList<>();
        for(Object object: (ArrayList) body){
            LinkedHashMap linkedHashMap = (LinkedHashMap) object;
            AddPreferenceRequest addPreferenceRequest = mapperUtil.convertObject(linkedHashMap, AddPreferenceRequest.class);
            if (StringUtils.isBlank(addPreferenceRequest.getCategory())) {
                log.error("Preference category missing");
                throw new MandatoryFieldMissingException("Preference","category");
            }
            if (StringUtils.isBlank(addPreferenceRequest.getPfKey())) {
                log.error("Preference key missing");
                throw new MandatoryFieldMissingException("Preference","key");
            }
            if (StringUtils.isBlank(addPreferenceRequest.getPfValue())) {
                log.error("Preference value missing");
                throw new MandatoryFieldMissingException("Preference","value");
            }
            Preferences preference = preferencesService.addPreference(addPreferenceRequest);
            preferencesList.add(buildPreferenceResponse(preference));
        }
        return ResponseEntity.status(HttpStatus.OK).body(preferencesList);
    }

    @Override
    public ResponseEntity<Object> editPreference(Object body) {
        log.info("Request to edit preference");
        PreferenceRequest preferenceRequest = mapperUtil.convertObject(body, PreferenceRequest.class);
        if (Objects.isNull(preferenceRequest.getId())) {
            log.error("Preference id missing");
            throw new MandatoryFieldMissingException("Preference", "id");
        }
        if(!preferencesRepository.existsById(preferenceRequest.getId())){
            log.error("Preference not found");
            throw new PreferenceNotFoundException();
        }
        Preferences preference = preferencesService.editPreference(preferenceRequest);
        return ResponseEntity.status(HttpStatus.OK).body(buildPreferenceResponse(preference));
    }

    @Override
    public ResponseEntity<Object> searchPreferences(SearchPreferenceRequest searchRequest) {
        log.info("Request to search preferences");
        List<Preferences> preferencesList = null;
        if(searchRequest != null){
            preferencesList = preferencesService.searchPreferences(searchRequest);
        }else{
            preferencesList = preferencesService.findAll();
        }
        List<PreferenceResponse> responses =  preferencesList.stream().map(obj -> buildPreferenceResponse(obj)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Override
    public ResponseEntity<Object> deletePreference(Long id) {
        log.info("Request to delete preference");
        if (Objects.isNull(id)) {
            log.error("Preference id missing");
            throw new MandatoryFieldMissingException("Preference", "id");
        }
        if(!preferencesRepository.existsById(id)){
            log.error("Preference not found");
            throw new PreferenceNotFoundException();
        }
        Preferences preference = preferencesService.deletePreference(id);
        return ResponseEntity.status(HttpStatus.OK).body(buildPreferenceResponse(preference));
    }

    private PreferenceResponse buildPreferenceResponse(Preferences preference){
        PreferenceResponse customerResponse = PreferenceResponse.builder()
            .id(preference.getId())
            .category(preference.getCategory())
            .pfKey(preference.getPfKey())
            .pfValue(preference.getPfValue())
            .createdOn(preference.getCreatedOn() != null ? preference.getCreatedOn().toString() : null)
            .createdOnTextForm(preference.getCreatedOn() != null ? dateTimeUtil.getDateDifferenceInReadableFormat(preference.getCreatedOn(), LocalDateTime.now()) : null)
            .createdBy(preference.getCreatedBy())
            .updatedOn(preference.getUpdatedOn() != null ? preference.getUpdatedOn().toString() : null)
            .updatedBy(preference.getUpdatedBy())
            .build();
        return customerResponse;
    }

}
