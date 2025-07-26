package com.rlabs.crm.service.product;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Product;
import com.rlabs.crm.payload.request.product.AddProductRequest;
import com.rlabs.crm.repository.ProductRepository;
import com.rlabs.crm.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public List<Product> findAllById(List<Long> ids){
        return productRepository.findAllById(ids);
    }

    public Product findById(Long id){
        return productRepository.findById(id).orElse(null);
    }

    public Product delete(Long id){
        log.debug("Deleting product. Product id: {}",id);
        Product product = findById(id);
        productRepository.deleteById(id);
        log.debug("Product deleted");
        return product;
    }

    @Transactional
    public List<Product> deleteAll(List<Long> ids){
        List<Product> productList = findAllById(ids);
        productRepository.deleteAllById(ids);
        return productList;
    }

    public Product buildProduct(AddProductRequest request){
        if(request == null){
            return null;
        }
        return Product.builder()
            .uid(Constants.THREE_CHAR_PRODUCT +dateTimeUtil.convertLocalDateTimeToString(LocalDateTime.now(), Constants.DATE_TIME_FORMAT_yyyyMMddHHmmss))
            .name(request.getName() != null ? request.getName() : null)
            .custPrdNumber(request.getCustPrdNumber() != null ? request.getCustPrdNumber() : null)
            .category(request.getCategory() != null ? request.getCategory() : null)
            .type(request.getType() != null ? request.getType() : null)
            .capsuleSize(request.getCapsuleSize() != null ? request.getCapsuleSize() : null)
            .packagingType(request.getPackagingType() != null ? request.getPackagingType() : null)
            .countPerBottle(request.getCountPerBottle() != null ? request.getCountPerBottle() : null)
            .batchSize(request.getBatchSize() != null ? request.getBatchSize() : null)
            .dosagePerUnit(request.getDosagePerUnit() != null ? request.getDosagePerUnit() : null)
            .dynamicFields(request.getDynamicFields() != null ? request.getDynamicFields() : null)
            .materialCost(request.getMaterialCost() != null ? request.getMaterialCost() : null)
            .processLoss(request.getProcessLoss() != null ? request.getProcessLoss() : null)
            .fillerCost(request.getFillerCost() != null ? request.getFillerCost() : null)
            .freightCharges(request.getFreightCharges() != null ? request.getFreightCharges() : null)
            .markUp(request.getMarkUp() != null ? request.getMarkUp() : null)
            .capsuleFillingCost(request.getCapsuleFillingCost() != null ? request.getCapsuleFillingCost() : null)
            .packagingCost(request.getPackagingCost() != null ? request.getPackagingCost() : null)
            .testingCost(request.getTestingCost() != null ? request.getTestingCost() : null)
            .stabilityCost(request.getStabilityCost() != null ? request.getStabilityCost() : null)
            .fulfillmentCost(request.getFulfillmentCost() != null ? request.getFulfillmentCost() : null)
            .build();
    }
}
