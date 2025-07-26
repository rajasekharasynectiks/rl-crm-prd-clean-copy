package com.rlabs.crm.repository;

import com.rlabs.crm.domain.CustomerNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerNoteRepository extends JpaRepository<CustomerNote, Long> {
    List<CustomerNote> findByCustomerId(Long customerId);
}
