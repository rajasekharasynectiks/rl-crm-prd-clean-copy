package com.rlabs.crm.repository;

import com.rlabs.crm.domain.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
    Optional<List<Preferences>> findByCategory(String category);
    Optional<List<Preferences>> findByPfKey(String pfKey);
    Optional<List<Preferences>> findByCategoryAndPfKey(String category, String pfKey);
    Optional<Preferences> findByCategoryAndPfKeyAndPfValue(String category, String pfKey, String pfValue);
}
