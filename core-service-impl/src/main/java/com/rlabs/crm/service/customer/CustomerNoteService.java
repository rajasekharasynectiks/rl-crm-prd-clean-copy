package com.rlabs.crm.service.customer;

import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.domain.CustomerNote;
import com.rlabs.crm.domain.QuotationAuditHistory;
import com.rlabs.crm.payload.request.customer.CustomerNoteRequest;
import com.rlabs.crm.repository.CustomerNoteRepository;
import com.rlabs.crm.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CustomerNoteService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerNoteRepository customerNoteRepository;

    @Autowired
    private EntityManager entityManager;


    public List<CustomerNote> findAll(){
        return customerNoteRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<CustomerNote> findAllById(List<Long> ids){
        return customerNoteRepository.findAllById(ids);
    }

    public CustomerNote findById(Long id){
        return customerNoteRepository.findById(id).orElse(null);
    }

    public List<CustomerNote> findByCustomerId(Long customerId) {
        return customerNoteRepository.findByCustomerId(customerId);
    }

    @Transactional
    public CustomerNote addCustomerNote(CustomerNoteRequest request) {
        log.debug("Adding new customer note");
        Customer customer = customerRepository.findById(request.getCustomerId()).get();
        CustomerNote customerNote = CustomerNote.builder()
            .notes(request.getNotes())
            .customer(customer)
            .build();
        customerNote = customerNoteRepository.save(customerNote);
        log.debug("New customer note added");
        return customerNote;
    }

    @Transactional
    public CustomerNote editCustomerNote(CustomerNoteRequest request) {
        log.debug("Updating customer note. Customer id: {}, note id: {}",request.getCustomerId(), request.getId());
        CustomerNote customerNote = customerNoteRepository.findById(request.getId()).get();
        if(!StringUtils.isBlank(request.getNotes())){
            customerNote.setNotes(request.getNotes());
        }
        customerNote = customerNoteRepository.save(customerNote);
        log.debug("Customer note updated");
        return customerNote;
    }

    public List<CustomerNote> searchCustomerNotes(CustomerNoteRequest request) {
        log.debug("Searching customer note of a customer. Customer id: {}",request.getCustomerId());
        return findByCustomerId(request.getCustomerId());
    }

    @Transactional
    public CustomerNote deleteCustomerNote(Long id){
        log.debug("Deleting customer note. Customer note id: {}",id);
        CustomerNote customerNote = customerNoteRepository.findById(id).get();

        String delQry = "delete from customer_notes where id = ? ";
        Query queryDelCustomerNotes = entityManager.createNativeQuery(delQry, CustomerNote.class);
        queryDelCustomerNotes.setParameter(1, id);
        queryDelCustomerNotes.executeUpdate();

        log.debug("Customer note deleted");
        return customerNote;
    }

    @Transactional
    public void deleteAllCustomerNote(Long customerId){
        String delQry = "delete from customer_notes where customer_id = ? ";
        Query queryDelCustomerNotes = entityManager.createNativeQuery(delQry, CustomerNote.class);
        queryDelCustomerNotes.setParameter(1, customerId);
        queryDelCustomerNotes.executeUpdate();
        log.debug("Customer notes deleted");

    }

}
