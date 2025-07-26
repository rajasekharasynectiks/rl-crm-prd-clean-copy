package com.rlabs.crm.controller.customer;

import com.rlabs.crm.api.controller.CustomerApi;
import com.rlabs.crm.api.model.SearchCustomerRequest;
import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.payload.request.customer.AddCustomerRequest;
import com.rlabs.crm.payload.request.customer.CustomerRequest;
import com.rlabs.crm.payload.request.customer.FavouriteCustomerRequest;
import com.rlabs.crm.payload.response.customer.CustomerAuditHistoryResponse;
import com.rlabs.crm.payload.response.customer.CustomerResponse;
import com.rlabs.crm.repository.CustomerRepository;
import com.rlabs.crm.service.customer.CustomerAuditHistoryService;
import com.rlabs.crm.service.customer.CustomerService;
import com.rlabs.crm.util.DateTimeUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.BadRequestAlertException;
import com.rlabs.crm.web.rest.errors.MandatoryFieldMissingException;
import com.rlabs.crm.web.rest.errors.customer.CustomerNotFoundException;
import com.rlabs.crm.web.rest.errors.file.FileDeletionFailedException;
import com.rlabs.crm.web.rest.errors.file.FileDownFailedException;
import com.rlabs.crm.web.rest.errors.file.FileUploadFailedException;
import com.rlabs.crm.web.rest.errors.security.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class CustomerController implements CustomerApi {

    @Value("${file-path.image}")
    private String filePathImage;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerAuditHistoryService customerAuditHistoryService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Override
    public ResponseEntity<Object> addCustomer(Object body) {
        log.info("Request to create new customer");
        AddCustomerRequest addCustomerRequest = mapperUtil.convertObject(body, AddCustomerRequest.class);
        if (StringUtils.isBlank(addCustomerRequest.getName())) {
            log.error("Customer name missing");
            throw new MandatoryFieldMissingException("Customer","name");
        }
        Customer customer = customerService.addCustomer(addCustomerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> editCustomer(Object body) {
        log.info("Request to edit customer");
        CustomerRequest customerRequest = mapperUtil.convertObject(body, CustomerRequest.class);
        if (Objects.isNull(customerRequest.getId())) {
            log.error("Customer id missing");
            throw new MandatoryFieldMissingException("Customer", "id");
        }
        if(!customerRepository.existsById(customerRequest.getId())){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        Customer customer = customerService.editCustomer(customerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> searchCustomers(SearchCustomerRequest searchRequest) {
        log.info("Request to search customers");
        List<Customer> customerList = null;
        if(searchRequest != null){
            customerList = customerService.searchCustomers(searchRequest);
        }else{
            customerList = customerService.findAll();
        }
        List<CustomerResponse> responses =  customerList.stream().map(obj -> CustomerResponse.buildCustomerResponse(obj, dateTimeUtil)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Override
    public ResponseEntity<Object> deleteCustomer(Long id) {
        log.info("Request to delete customer");
        if (Objects.isNull(id)) {
            log.error("Customer id missing");
            throw new BadRequestAlertException("Customer id missing", "customer", "nullcustomerid");
        }
        if(!customerRepository.existsById(id)){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        Customer customer = customerService.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> setCustomerArchive(Long id) {
        log.info("Request to archive a customer");
        if (Objects.isNull(id)) {
            log.error("Customer id missing");
            throw new BadRequestAlertException("Customer id missing", "customer", "nullcustomerid");
        }
        if(!customerRepository.existsById(id)){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        Customer customer = customerService.setCustomerArchive(id);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> setCustomerActive(Long id) {
        log.info("Request to active a customer");
        if (Objects.isNull(id)) {
            log.error("Customer id missing");
            throw new BadRequestAlertException("Customer id missing", "customer", "nullcustomerid");
        }
        if(!customerRepository.existsById(id)){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        Customer customer = customerService.setCustomerActive(id);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> setUnsetFavouriteCustomer(Object body) {
        log.info("Request to set or unset favourite customer");
        FavouriteCustomerRequest favouriteCustomerRequest = mapperUtil.convertObject(body, FavouriteCustomerRequest.class);
        if (Objects.isNull(favouriteCustomerRequest.getId())) {
            log.error("Customer id missing");
            throw new BadRequestAlertException("Customer id missing", "Customer", "nullcustomerid");
        }
        if(!customerRepository.existsById(favouriteCustomerRequest.getId())){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        Customer customer = customerService.setUnsetFavouriteCustomer(favouriteCustomerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerResponse.buildCustomerResponse(customer, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> uploadCustomerProfileImage(Long id, MultipartFile file) {
        log.info("Request to upload customer profile image. User id: {}", id);
        if (Objects.isNull(id)) {
            log.error("Customer id missing");
            throw new MandatoryFieldMissingException("Customers", "id");
        }
        if(!customerRepository.existsById(id)){
            log.error("Customer not found");
            throw new UserNotFoundException();
        }
        Customer customer = customerService.findById(id);
        try {
            Path projectDir = Paths.get(System.getProperty("user.dir"));
            Path newDir = projectDir.resolve(filePathImage+ File.separatorChar+"customers");
            if (!Files.exists(newDir)) {
                Files.createDirectories(newDir);
            }
            boolean response = customerService.saveFile(newDir, customer, file);
            return ResponseEntity.ok("File saved");
        } catch (Exception e) {
            log.error("Failed to upload file. ", e);
            throw new FileUploadFailedException();
        }
    }

    @Override
    public ResponseEntity<Object> deleteCustomerProfileImage(Long id) {
        log.info("Request to delete customer profile image. User id: {}", id);
        if (Objects.isNull(id)) {
            log.error("Customer id missing");
            throw new MandatoryFieldMissingException("Customers", "id");
        }
        if(!customerRepository.existsById(id)){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        Customer customer = customerService.findById(id);
        try {
            boolean response = customerService.deleteFile(customer);
            return ResponseEntity.ok("File deleted");
        } catch (Exception e) {
            log.error("Failed to delete file. ", e);
            throw new FileDeletionFailedException();
        }
    }

    @Override
    public ResponseEntity<Object> getCustomerProfileImage(Long id) {
        log.info("Request to get customer profile image. User id: {}", id);
        if (Objects.isNull(id)) {
            log.error("Customer id missing");
            throw new MandatoryFieldMissingException("Customers", "id");
        }
        if(!customerRepository.existsById(id)){
            log.error("Customer not found");
            throw new UserNotFoundException();
        }
        Customer customer = customerService.findById(id);
        try {
            byte[] file  = customerService.getFile(customer);
            return ResponseEntity.ok(Base64.encodeBase64String(file));
        } catch (Exception e) {
            log.error("Failed to retrieve file. ", e);
            throw new FileDownFailedException();
        }
    }

    @Override
    public ResponseEntity<Object> getCustomerAuditHistory(Long id) {
        log.info("Request to get customer history");
        if (Objects.isNull(id)) {
            log.error("Customer id missing");
            throw new MandatoryFieldMissingException("Customer", "id");
        }
        if(!customerRepository.existsById(id)){
            log.error("Customer not found");
            throw new CustomerNotFoundException();
        }
        List<CustomerAuditHistoryResponse> responseList = customerAuditHistoryService.findByCustomerId(id).stream().map(obj -> CustomerAuditHistoryResponse.buildResponse(obj, dateTimeUtil)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }
}
