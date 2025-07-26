package com.rlabs.crm.service.preference;

import com.rlabs.crm.api.model.SearchPreferenceRequest;
import com.rlabs.crm.domain.Preferences;
import com.rlabs.crm.payload.request.preferences.AddPreferenceRequest;
import com.rlabs.crm.payload.request.preferences.PreferenceRequest;
import com.rlabs.crm.repository.PreferencesRepository;
import com.rlabs.crm.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PreferencesService {

    @Autowired
    private PreferencesRepository preferencesRepository;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    public List<Preferences> findAll(){
        return preferencesRepository.findAll();
    }

    public List<Preferences> findAllById(List<Long> ids){
        return preferencesRepository.findAllById(ids);
    }

    public Preferences findById(Long id){
        return preferencesRepository.findById(id).orElse(null);
    }

    @Transactional
    public Preferences addPreference(AddPreferenceRequest request) {
        log.debug("Adding new preferences");
        Preferences preferences = Preferences.builder()
            .category(request.getCategory())
            .pfKey(request.getPfKey())
            .pfValue(request.getPfValue())
            .build();
        preferences = preferencesRepository.save(preferences);
        log.debug("New preferences added");
        return preferences;
    }

    @Transactional
    public Preferences editPreference(PreferenceRequest request) {
        log.debug("Updating preferences. Preferences id: {}",request.getId());
        Preferences preferences = preferencesRepository.findById(request.getId()).get();
        if(!StringUtils.isBlank(request.getCategory())){
            preferences.setCategory(request.getCategory());
        }
        if(!StringUtils.isBlank(request.getPfKey())){
            preferences.setPfKey(request.getPfKey());
        }
        if(!StringUtils.isBlank(request.getPfValue())){
            preferences.setPfValue(request.getPfValue());
        }
        preferences = preferencesRepository.save(preferences);
        log.debug("Preference updated");
        return preferences;
    }

    public List<Preferences> searchPreferences(SearchPreferenceRequest request) {
        log.debug("Searching preferences");
        Preferences preference = Preferences.builder().build();
        if(request.getId() != null){
            preference.setId(request.getId());
        }
        if(!StringUtils.isBlank(request.getCategory())){
            preference.setCategory(request.getCategory());
        }
        if(!StringUtils.isBlank(request.getPfKey())){
            preference.setPfKey(request.getPfKey());
        }
        if(!StringUtils.isBlank(request.getPfValue())){
            preference.setPfValue(request.getPfValue());
        }
        if(!StringUtils.isBlank(request.getCreatedBy())){
            preference.setCreatedBy(request.getCreatedBy());
        }
        if(!StringUtils.isBlank(request.getUpdatedBy())){
            preference.setUpdatedBy(request.getUpdatedBy());
        }

        Example example = Example.of(preference);
        List<Preferences> customerList = preferencesRepository.findAll(example, Sort.by(Sort.Direction.DESC, "id"));
        log.debug("Preferences list");
        return customerList;
    }

    @Transactional
    public Preferences deletePreference(Long id){
        log.debug("Deleting preference. Preferences id: {}",id);
        Preferences preference = preferencesRepository.findById(id).get();
        preferencesRepository.deleteById(id);
        log.debug("Preferences deleted");
        return preference;
    }


}
