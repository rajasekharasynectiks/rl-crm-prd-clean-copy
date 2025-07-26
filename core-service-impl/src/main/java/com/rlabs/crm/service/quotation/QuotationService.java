package com.rlabs.crm.service.quotation;

import com.rlabs.crm.api.model.SearchQuotationRequest;
import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.*;
import com.rlabs.crm.payload.request.quotation.AddQuotationRequest;
import com.rlabs.crm.payload.request.quotation.QuotationRequest;
import com.rlabs.crm.payload.request.quotation.ReviewQuotationRequest;
import com.rlabs.crm.repository.QuotationRepository;
import com.rlabs.crm.service.customer.CustomerService;
import com.rlabs.crm.service.product.ProductIngredientService;
import com.rlabs.crm.service.product.ProductPackagingDetailService;
import com.rlabs.crm.service.product.ProductService;
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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class QuotationService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductIngredientService productIngredientService;

    @Autowired
    private QuotationAuditHistoryService quotationAuditHistoryService;

    @Autowired
    private ProductPackagingDetailService productPackagingDetailService;

    public List<Quotation> findAll(){
        return quotationRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Quotation> findAllById(List<Long> ids){
        return quotationRepository.findAllById(ids);
    }

    public Quotation findById(Long id){
        return quotationRepository.findById(id).orElse(null);
    }

    @Transactional
    public Quotation addQuotation(AddQuotationRequest request) {
        log.debug("Adding new quotation");

        Quotation quotation = Quotation.builder()
            .uid(Constants.THREE_CHAR_QUOTATION +dateTimeUtil.convertLocalDateTimeToString(LocalDateTime.now(), Constants.DATE_TIME_FORMAT_yyyyMMddHHmmss))
            .qtNumber(!StringUtils.isBlank(request.getQtNumber()) ? request.getQtNumber() : null)
            .qtOwner(!StringUtils.isBlank(request.getQtOwner()) ? request.getQtOwner() : null)
            .status(request.getStatus().toUpperCase())
            .qtExpiryDate(!StringUtils.isBlank(request.getQtExpiryDate()) ? dateTimeUtil.convertStringToLocalDate(request.getQtExpiryDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .qtDate(!StringUtils.isBlank(request.getQtDate()) ? dateTimeUtil.convertStringToLocalDate(request.getQtDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd) : null)
            .dynamicFields(request.getDynamicFields())
            .version(1)
            .customer(request.getCustomerId() != null ? customerService.findById(request.getCustomerId()) : null)
            .product(productService.buildProduct(request.getAddProductRequest()))
            .build();

        quotation = quotationRepository.save(quotation);

        if(request.getAddProductRequest() != null && request.getAddProductRequest().getAddProductIngredientRequests() != null
                && !request.getAddProductRequest().getAddProductIngredientRequests().isEmpty()){
            final Product product = quotation.getProduct(); // lambda accept a final object
            List<ProductIngredient> productIngredientList =  request.getAddProductRequest().getAddProductIngredientRequests()
                .stream().map(obj -> productIngredientService.buildProductIngredient(obj))
                .toList().stream().peek(obj -> obj.setProduct(product)).toList();

            quotation.getProduct().setIngredients(productIngredientService.addAllProductIngredients(productIngredientList));

        }
        if(request.getAddProductRequest() != null && request.getAddProductRequest().getAddProductPackagingDetailRequests() != null
            && !request.getAddProductRequest().getAddProductPackagingDetailRequests().isEmpty()){
            final Product product = quotation.getProduct(); // lambda accept a final object
            List<ProductPackagingDetail> productPackagingDetailList =  request.getAddProductRequest().getAddProductPackagingDetailRequests()
                .stream().map(obj -> productPackagingDetailService.buildProductPackagingDetail(obj))
                .toList().stream().peek(obj -> obj.setProduct(product)).toList();

            quotation.getProduct().setProductPackagingDetails(productPackagingDetailService.addAllProductPackagingDetail(productPackagingDetailList));

        }
        quotationAuditHistoryService.addQuotationAuditHistory(quotation, null, Constants.INSERT);
        log.debug("New quotation added");
        return quotation;
    }

    @Transactional
    public Quotation editQuotation(QuotationRequest request) {
        log.debug("Updating quotation. Quotation id: {}",request.getId());
        Quotation quotation = quotationRepository.findById(request.getId()).get();
        Quotation oldQuotation = new Quotation();
        BeanUtils.copyProperties(quotation, oldQuotation);
        if(!StringUtils.isBlank(request.getQtNumber())){
            quotation.setQtNumber(request.getQtNumber());
        }
        if(!StringUtils.isBlank(request.getQtOwner())){
            quotation.setQtOwner(request.getQtOwner());
        }
        if(!StringUtils.isBlank(request.getStatus())){
            quotation.setStatus(request.getStatus());
        }
        if(!StringUtils.isBlank(request.getQtExpiryDate())){
            quotation.setQtExpiryDate(dateTimeUtil.convertStringToLocalDate(request.getQtExpiryDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd));
        }
        if(!StringUtils.isBlank(request.getQtDate())){
            quotation.setQtDate(dateTimeUtil.convertStringToLocalDate(request.getQtDate(), Constants.DATE_TIME_FORMAT_yyyy_MM_dd));
        }
        if(request.getDynamicFields() != null){
            quotation.setDynamicFields(request.getDynamicFields());
        }
        quotation = quotationRepository.save(quotation);
        quotationAuditHistoryService.addQuotationAuditHistory(quotation, oldQuotation, Constants.UPDATE);
        log.debug("Quotation updated");
        return quotation;
    }

    @Transactional
    public Quotation deleteQuotation(Long id){
        log.debug("Deleting quotation. Quotation id: {}",id);
        Quotation quotation = quotationRepository.findById(id).get();

        String delQtAuditHist = "delete from quotations_audit_history where quotation_id = ? ";
        Query queryDelQtAuditHist = entityManager.createNativeQuery(delQtAuditHist, QuotationAuditHistory.class);
        queryDelQtAuditHist.setParameter(1, quotation.getId());
        queryDelQtAuditHist.executeUpdate();
        log.debug("Quotation audit history deleted");

        String delQt = "delete from quotations where id = ? ";
        Query queryDelQt = entityManager.createNativeQuery(delQt, Quotation.class);
        queryDelQt.setParameter(1, quotation.getId());
        queryDelQt.executeUpdate();
        log.debug("Quotation deleted");

        if(quotation.getProduct() != null){
            String delQtPrdIng = "delete from product_ingredients where product_id = ? ";
            Query queryDelQtPrdIng = entityManager.createNativeQuery(delQtPrdIng, ProductIngredient.class);
            queryDelQtPrdIng.setParameter(1, quotation.getProduct().getId());
            queryDelQtPrdIng.executeUpdate();
            log.debug("Product ingredients deleted");

            String delQtPrd = "delete from products where id = ? ";
            Query queryDelQtPrd = entityManager.createNativeQuery(delQtPrd, Product.class);
            queryDelQtPrd.setParameter(1, quotation.getProduct().getId());
            queryDelQtPrd.executeUpdate();
            log.debug("Product deleted");
        }

        log.debug("Quotation deleted successfully");
        return quotation;
    }

    public List<Quotation> searchQuotation(SearchQuotationRequest request) {
        log.debug("Searching quotations");
        StringBuilder primarySql = new StringBuilder("select q.* from quotations q where 1 = 1 ");

        if(request.getId() != null){
            primarySql.append(" and q.id = ? ");
        }
        if(request.getUid() != null){
            primarySql.append(" and upper(q.uid) = upper(?) ");
        }
        if(request.getQtNumber() != null){
            primarySql.append(" and upper(q.qt_number) like upper('%"+request.getQtNumber().trim()+"%') ");
        }
        if(request.getQtOwner() != null){
            primarySql.append(" and upper(q.qt_owner) like upper('%"+request.getQtOwner().trim()+"%') ");
        }
        if(!StringUtils.isBlank(request.getStatus())){
            primarySql.append(" and upper(q.status) = upper(?) ");
        }
        if(!StringUtils.isBlank(request.getQtDate())){
            primarySql.append(" and q.qt_date = cast(? as date) ");
        }
        if(!StringUtils.isBlank(request.getQtExpiryDate())){
            primarySql.append(" and q.qt_date = cast(? as date) ");
        }
        if(request.getVersion() != null){
            primarySql.append(" and q.version = ? ");
        }
        if(!StringUtils.isBlank(request.getCreatedBy())){
            primarySql.append(" and upper(q.created_by) = upper(?) ");
        }
        if(!StringUtils.isBlank(request.getUpdatedBy())){
            primarySql.append(" and upper(q.updated_by) = upper(?) ");
        }
        if(!StringUtils.isBlank(request.getCreatedOn())){
            primarySql.append(" and cast(q.created_on as date) = cast(? as date) ");
        }
        if(!StringUtils.isBlank(request.getUpdatedOn())){
            primarySql.append(" and cast(q.updated_on as date) = cast(? as date) ");
        }
        if(request.getCustomerId() != null){
            primarySql.append(" and q.customer_id = ? ");
        }
        primarySql.append(" order by id desc ");
        Query query = entityManager.createNativeQuery(primarySql.toString(), Quotation.class);
        int index = 0;
        if(request.getId() != null){
            query.setParameter(++index, request.getId());
        }
        if(request.getUid() != null){
            query.setParameter(++index, request.getUid());
        }

        if(!StringUtils.isBlank(request.getStatus())){
            query.setParameter(++index, request.getStatus());
        }
        if(!StringUtils.isBlank(request.getQtDate())){
            query.setParameter(++index, request.getQtDate());
        }
        if(!StringUtils.isBlank(request.getQtExpiryDate())){
            query.setParameter(++index, request.getQtExpiryDate());
        }
        if(request.getVersion() != null){
            query.setParameter(++index, request.getVersion().intValue());
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
        if(request.getCustomerId() != null){
            query.setParameter(++index, request.getCustomerId());
        }
        return query.getResultList();
    }

    @Transactional
    public Quotation reviewQuotation(ReviewQuotationRequest request) {
        log.debug("Updating quotation after review. Quotation id: {}",request.getId());
        Quotation quotation = quotationRepository.findById(request.getId()).get();

        if(!StringUtils.isBlank(request.getStatus())){
            quotation.setStatus(request.getStatus());
        }
        if(!StringUtils.isBlank(request.getComments())){
            quotation.setComments(request.getComments());
        }

        quotation = quotationRepository.save(quotation);
        log.debug("Quotation updated with review comments");
        return quotation;
    }


    public List<Document> searchQuotationDocuments(Long customerId, Long quotationId, Long productId, Long version ) {
        log.debug("Searching documents");
        StringBuilder primarySql = new StringBuilder("select d.* from documents d WHERE 1 = 1 ");
        if(customerId != null){
            primarySql.append(" and JSON_VALUE(d.source, '$.sourceId.customerId') = ? ");
        }
        if(quotationId != null){
            primarySql.append(" and JSON_VALUE(d.source, '$.sourceId.quotationId') = ? ");
        }
        if(productId != null){
            primarySql.append(" and JSON_VALUE(d.source, '$.sourceId.productId') = ? ");
        }
        if(version != null){
            primarySql.append(" and JSON_VALUE(d.source, '$.sourceId.version') = ? ");
        }
        Query query = entityManager.createNativeQuery(primarySql.toString(), Document.class);
        int index = 0;
        if(customerId != null){
            query.setParameter(++index, customerId);
        }
        if(quotationId != null){
            query.setParameter(++index, quotationId);
        }
        if(productId != null){
            query.setParameter(++index, productId);
        }
        if(version != null){
            query.setParameter(++index, version);
        }
        return query.getResultList();
    }
}
