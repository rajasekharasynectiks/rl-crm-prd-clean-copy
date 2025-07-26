package com.rlabs.crm.service.customer;

import com.rlabs.crm.api.model.SearchCustomerRequest;
import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.payload.request.customer.AddCustomerRequest;
import com.rlabs.crm.payload.request.customer.CustomerNoteRequest;
import com.rlabs.crm.payload.request.customer.CustomerRequest;
import com.rlabs.crm.payload.request.customer.FavouriteCustomerRequest;
import com.rlabs.crm.repository.CustomerRepository;
import com.rlabs.crm.util.DateTimeUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerNoteService customerNoteService;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerAuditHistoryService customerAuditHistoryService;

    public List<Customer> findAll(){
        return customerRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Customer> findAllById(List<Long> ids){
        return customerRepository.findAllById(ids);
    }

    public Customer findById(Long id){
        return customerRepository.findById(id).orElse(null);
    }

    @Transactional
    public Customer addCustomer(AddCustomerRequest request) {
        log.debug("Adding new customer");
        Customer customer = Customer.builder()
            .uid(Constants.THREE_CHAR_CUSTOMER +dateTimeUtil.convertLocalDateTimeToString(LocalDateTime.now(), Constants.DATE_TIME_FORMAT_yyyyMMddHHmmss))
            .name(!StringUtils.isBlank(request.getName()) ? request.getName() : null)
            .companyName(!StringUtils.isBlank(request.getCompanyName()) ? request.getCompanyName() : null)
            .companyAbrv(!StringUtils.isBlank(request.getCompanyAbrv()) ? request.getCompanyAbrv() : null)
            .phoneNo(!StringUtils.isBlank(request.getPhoneNo()) ? request.getPhoneNo() : null)
            .email(!StringUtils.isBlank(request.getEmail()) ? request.getEmail() : null)
            .address(!StringUtils.isBlank(request.getAddress()) ? request.getAddress() : null)
            .city(!StringUtils.isBlank(request.getCity()) ? request.getCity() : null)
            .zipCode(!StringUtils.isBlank(request.getZipCode()) ? request.getZipCode() : null)
            .country(!StringUtils.isBlank(request.getCountry()) ? request.getCountry() : null)
            .state(!StringUtils.isBlank(request.getState()) ? request.getState() : null)
            .isLead(request.getIsLead() == null ? false : request.getIsLead() )
            .isNew(true)
            .favourite(false)
            .status(Constants.STATUS_ACTIVE)
            .build();
        customer = customerRepository.save(customer);
        CustomerNoteRequest customerNoteRequest = CustomerNoteRequest.builder()
            .notes(request.getNotes())
            .customerId(customer.getId())
            .build();
        customerNoteService.addCustomerNote(customerNoteRequest);
        customerAuditHistoryService.addCustomerAuditHistory(customer, null, Constants.INSERT);
        log.debug("New customer added");
        return customer;
    }

    @Transactional
    public Customer editCustomer(CustomerRequest request) {
        log.debug("Updating customer. Customer id: {}",request.getId());
        Customer customer = customerRepository.findById(request.getId()).get();
        Customer oldCustomer = new Customer();
        BeanUtils.copyProperties(customer, oldCustomer);
        if(!StringUtils.isBlank(request.getName())){
            customer.setName(request.getName());
        }
        if(!StringUtils.isBlank(request.getCompanyAbrv())){
            customer.setCompanyName(request.getCompanyName());
        }
        if(!StringUtils.isBlank(request.getCompanyAbrv())){
            customer.setCompanyAbrv(request.getCompanyAbrv());
        }
        if(!StringUtils.isBlank(request.getPhoneNo())){
            customer.setPhoneNo(request.getPhoneNo());
        }
        if(!StringUtils.isBlank(request.getEmail())){
            customer.setEmail(request.getEmail());
        }
        if(!StringUtils.isBlank(request.getAddress())){
            customer.setAddress(request.getAddress());
        }
        if(!StringUtils.isBlank(request.getCity())){
            customer.setCity(request.getCity());
        }
        if(!StringUtils.isBlank(request.getZipCode())){
            customer.setZipCode(request.getZipCode());
        }
        if(!StringUtils.isBlank(request.getCountry())){
            customer.setCountry(request.getCountry());
        }
        if(!StringUtils.isBlank(request.getState())){
            customer.setState(request.getState());
        }
        if(request.getIsLead() != null){
            customer.setIsLead(request.getIsLead());
        }
        if(!StringUtils.isBlank(request.getStatus())){
            customer.setStatus(request.getStatus());
        }
        customer = customerRepository.save(customer);

        customerAuditHistoryService.addCustomerAuditHistory(customer, oldCustomer, Constants.UPDATE);
        log.debug("Customer updated");
        return customer;
    }

    public List<Customer> searchCustomers(SearchCustomerRequest request) {
        log.debug("Searching customers");

        StringBuilder primarySql = new StringBuilder("select c.* from customers c where 1 = 1 ");

        if(request.getId() != null){
            primarySql.append(" and c.id = ? ");
        }
        if(!StringUtils.isBlank(request.getUid())){
            primarySql.append(" and upper(c.uid) = upper(?) ");
        }
        if(!StringUtils.isBlank(request.getName())){
            primarySql.append(" and upper(c.name) like upper('%"+request.getName().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getCompanyName())){
            primarySql.append(" and upper(c.company_name) like upper('%"+request.getCompanyName().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getCompanyAbrv())){
            primarySql.append(" and upper(c.company_abrv) like upper('%"+request.getCompanyAbrv().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getPhoneNo())){
            primarySql.append(" and c.phone_no = ? ");
        }
        if(!StringUtils.isBlank(request.getEmail())){
            primarySql.append(" and c.email = ? ");
        }
        if(!StringUtils.isBlank(request.getAddress())){
            primarySql.append(" and upper(c.address) like upper('%"+request.getAddress().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getCity())){
            primarySql.append(" and upper(c.city) like upper('%"+request.getCity().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getZipCode())){
            primarySql.append(" and upper(c.zip_code) like upper('%"+request.getZipCode().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getCountry())){
            primarySql.append(" and upper(c.country) like upper('%"+request.getCountry().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getState())){
            primarySql.append(" and upper(c.state) like upper('%"+request.getState().trim()+"%') ");
        }
        if(request.getFavourite() != null){
            primarySql.append(" and c.favourite = ? ");
        }
        if(request.getIsNew() != null){
            primarySql.append(" and c.is_new = ? ");
        }
        if(request.getIsLead() != null){
            primarySql.append(" and c.is_lead = ? ");
        }
        if(!StringUtils.isBlank(request.getCreatedBy())){
            primarySql.append(" and upper(c.created_by) = upper(?) ");
        }
        if(!StringUtils.isBlank(request.getUpdatedBy())){
            primarySql.append(" and upper(c.updated_by) = upper(?) ");
        }
        if(!StringUtils.isBlank(request.getCreatedOn())){
            primarySql.append(" and cast(c.created_on as date) = cast(? as date) ");
        }
        if(!StringUtils.isBlank(request.getUpdatedOn())){
            primarySql.append(" and cast(c.updated_on as date) = cast(? as date) ");
        }
        primarySql.append(" order by id desc ");
        Query query = entityManager.createNativeQuery(primarySql.toString(), Customer.class);
        int index = 0;
        if(request.getId() != null){
            query.setParameter(++index, request.getId());
        }
        if(!StringUtils.isBlank(request.getUid())){
            query.setParameter(++index, request.getUid());
        }
        if(!StringUtils.isBlank(request.getPhoneNo())){
            query.setParameter(++index, request.getPhoneNo());
        }
        if(!StringUtils.isBlank(request.getEmail())){
            query.setParameter(++index, request.getEmail());
        }
        if(request.getFavourite() != null){
            query.setParameter(++index, request.getFavourite());
        }
        if(request.getIsNew() != null){
            query.setParameter(++index, request.getIsNew());
        }
        if(request.getIsLead() != null){
            query.setParameter(++index, request.getIsLead());
        }
        if(!StringUtils.isBlank(request.getCreatedBy())){
            query.setParameter(++index, request.getCreatedBy());
        }
        if(!StringUtils.isBlank(request.getUpdatedBy())){
            query.setParameter(++index, request.getUpdatedBy());
        }
        if(!StringUtils.isBlank(request.getCreatedOn())){
            query.setParameter(++index, request.getCreatedOn());
        }
        if(!StringUtils.isBlank(request.getUpdatedOn())){
            query.setParameter(++index, request.getUpdatedOn());
        }
        return query.getResultList();
    }

    @Transactional
    public Customer deleteCustomer(Long id){
        log.debug("Deleting customer. Customer id: {}",id);
        Customer customer = customerRepository.findById(id).get();
        customerRepository.deleteById(id);
        log.debug("Customer deleted");
        return customer;
    }

    @Transactional
    public Customer setCustomerArchive(Long id){
        log.debug("Changing customer status to archive. Customer id: {}",id);
        Customer customer = customerRepository.findById(id).get();
        Customer oldCustomer = new Customer();
        BeanUtils.copyProperties(customer, oldCustomer);
        customer.setStatus(Constants.STATUS_ARCHIVE);
        customer = customerRepository.save(customer);
        customerAuditHistoryService.addCustomerAuditHistory(customer, oldCustomer, Constants.UPDATE);
        log.debug("Customer archived");
        return customer;
    }

    @Transactional
    public Customer setCustomerActive(Long id){
        log.debug("Changing customer status to active. Customer id: {}",id);
        Customer customer = customerRepository.findById(id).get();
        Customer oldCustomer = new Customer();
        BeanUtils.copyProperties(customer, oldCustomer);
        customer.setStatus(Constants.STATUS_ACTIVE);
        customer = customerRepository.save(customer);
        customerAuditHistoryService.addCustomerAuditHistory(customer, oldCustomer, Constants.UPDATE);
        log.debug("Customer active");
        return customer;
    }

    @Transactional
    public Customer setUnsetFavouriteCustomer(FavouriteCustomerRequest request) {
        log.debug("Updating customer as favourite or not. Customer id: {}, favourite flag: {}",request.getId(), request.getIsFavourite());
        Customer customer = customerRepository.findById(request.getId()).get();
        Customer oldCustomer = new Customer();
        BeanUtils.copyProperties(customer, oldCustomer);
        if(request.getIsFavourite() != null){
            customer.setFavourite(request.getIsFavourite());
        }
        customer = customerRepository.save(customer);
        customerAuditHistoryService.addCustomerAuditHistory(customer, oldCustomer, Constants.UPDATE);
        log.debug("Customer updated");
        return customer;
    }

    @Transactional
    public boolean saveFile(Path newDir, Customer customer, MultipartFile file) throws IOException {
        Customer oldCustomer = new Customer();
        BeanUtils.copyProperties(customer, oldCustomer);
        String extension= file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String fileName = customer.getName()+"_"+customer.getId()+"."+extension;
        Path path = Paths.get(newDir.toString() + File.separatorChar + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        customer.setProfileImageFileName(fileName);
        customer.setProfileImageAccessUri(newDir.toString());
        customer.setProfileImageLocation(Constants.LOCAL_STORAGE);
        customerRepository.save(customer);
        customerAuditHistoryService.addCustomerAuditHistory(customer, oldCustomer, Constants.UPDATE);
        return true;
    }

    @Transactional
    public boolean deleteFile(Customer customer) throws IOException {
        if(Constants.LOCAL_STORAGE.equalsIgnoreCase(customer.getProfileImageLocation())){
            Customer oldCustomer = new Customer();
            BeanUtils.copyProperties(customer, oldCustomer);
            Path path = Paths.get(customer.getProfileImageAccessUri() + File.separatorChar + customer.getProfileImageFileName());
            Files.delete(path);
            customer.setProfileImageAccessUri(null);
            customer.setProfileImageFileName(null);
            customer.setProfileImageLocation(null);
            customerRepository.save(customer);
            customerAuditHistoryService.addCustomerAuditHistory(customer, oldCustomer, Constants.UPDATE);
            return true;
        }
        return false;
    }

    @Transactional
    public byte[] getFile(Customer customer) throws IOException {
        if(Constants.LOCAL_STORAGE.equalsIgnoreCase(customer.getProfileImageLocation())){
            Path path = Paths.get(customer.getProfileImageAccessUri() + File.separatorChar + customer.getProfileImageFileName());
            return Files.readAllBytes(path);
        }
        return null;
    }

}
