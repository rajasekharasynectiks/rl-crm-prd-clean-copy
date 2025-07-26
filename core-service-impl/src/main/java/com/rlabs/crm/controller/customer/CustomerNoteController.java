package com.rlabs.crm.controller.customer;

import com.rlabs.crm.api.controller.CustomerNoteApi;
import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.domain.CustomerNote;
import com.rlabs.crm.payload.request.customer.CustomerNoteRequest;
import com.rlabs.crm.payload.response.customer.CustomerResponse;
import com.rlabs.crm.repository.CustomerNoteRepository;
import com.rlabs.crm.repository.CustomerRepository;
import com.rlabs.crm.service.customer.CustomerNoteService;
import com.rlabs.crm.service.customer.CustomerService;
import com.rlabs.crm.util.DateTimeUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.MandatoryFieldMissingException;
import com.rlabs.crm.web.rest.errors.customer.CustomerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class CustomerNoteController implements CustomerNoteApi {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerNoteRepository customerNoteRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerNoteService customerNoteService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Override
    public ResponseEntity<Object> addCustomerNote(Object body) {
        log.info("Request to create new customer note");
        CustomerNoteRequest customerNoteRequest = mapperUtil.convertObject(body, CustomerNoteRequest.class);
        if (customerNoteRequest.getCustomerId() == null) {
            log.error("Customer id missing");
            throw new MandatoryFieldMissingException("Customer","id");
        }
        if(!customerRepository.existsById(customerNoteRequest.getCustomerId())){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        customerNoteService.addCustomerNote(customerNoteRequest);
        Customer customer = customerService.findById(customerNoteRequest.getCustomerId());
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> editCustomerNote(Object body) {
        log.info("Request to edit customer");
        CustomerNoteRequest customerNoteRequest = mapperUtil.convertObject(body, CustomerNoteRequest.class);
        if (Objects.isNull(customerNoteRequest.getId())) {
            log.error("Customer note id missing");
            throw new MandatoryFieldMissingException("CustomerNote", "id");
        }
        if(!customerRepository.existsById(customerNoteRequest.getCustomerId())){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        CustomerNote customerNote = customerNoteService.editCustomerNote(customerNoteRequest);
        Customer customer = customerService.findById(customerNoteRequest.getCustomerId());
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> searchCustomerNotes(Long customerId) {
        log.info("Request to search customer notes");

        if(!Objects.isNull(customerId)) {
            Customer customer = customerService.findById(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
        }
        List<Customer> customerList = customerService.findAll();
        List<CustomerResponse> responses =  customerList.stream().map(obj -> CustomerResponse.buildCustomerResponse(obj, dateTimeUtil)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Override
    public ResponseEntity<Object> deleteCustomerNote(Long id) {
        log.info("Request to delete customer note");
        if (Objects.isNull(id)) {
            log.error("Customer note id missing");
            throw new MandatoryFieldMissingException("CustomerNote", "id");
        }
        if(!customerNoteRepository.existsById(id)){
            log.error("Customer note not found");
            throw new CustomerNotFoundException();
        }
        CustomerNote customerNote = customerNoteService.deleteCustomerNote(id);
        Customer customer = customerService.findById(customerNote.getCustomer().getId());
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> deleteAllCustomerNote(Long customerId) {
        log.info("Request to delete all notes of a customer. Customer id: {}",customerId);
        if (Objects.isNull(customerId)) {
            log.error("Customer id missing");
            throw new MandatoryFieldMissingException("Customer", "id");
        }
        if(!customerRepository.existsById(customerId)){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        customerNoteService.deleteAllCustomerNote(customerId);
        Customer customer = customerService.findById(customerId);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }
}
