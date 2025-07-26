package com.rlabs.crm.service.product;

import com.rlabs.crm.domain.ProductPackagingDetail;
import com.rlabs.crm.payload.request.product.AddProductPackagingDetailRequest;
import com.rlabs.crm.repository.ProductPackagingDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ProductPackagingDetailService {

    @Autowired
    private ProductPackagingDetailRepository productPackagingDetailRepository;

    public List<ProductPackagingDetail> findAll(){
        return productPackagingDetailRepository.findAll();
    }

    public List<ProductPackagingDetail> findAllById(List<Long> ids){
        return productPackagingDetailRepository.findAllById(ids);
    }

    public ProductPackagingDetail findById(Long id){
        return productPackagingDetailRepository.findById(id).orElse(null);
    }

    @Transactional
    public ProductPackagingDetail delete(Long id){
        log.debug("Deleting product packaging detail. ProductPackagingDetail id: {}",id);
        ProductPackagingDetail productPackagingDetail = findById(id);
        productPackagingDetailRepository.deleteById(id);
        log.debug("Product packaging detail deleted");
        return productPackagingDetail;
    }


    public List<ProductPackagingDetail> deleteAll(List<Long> ids){
        List<ProductPackagingDetail> productPackagingDetailList = findAllById(ids);
        productPackagingDetailRepository.deleteAllById(ids);
        return productPackagingDetailList;
    }

    public List<ProductPackagingDetail> addAllProductPackagingDetail(List<ProductPackagingDetail> productPackagingDetailList){
        log.debug("Adding all product packaging detail");
        return productPackagingDetailRepository.saveAll(productPackagingDetailList);
    }

    public ProductPackagingDetail buildProductPackagingDetail(AddProductPackagingDetailRequest request){
        return ProductPackagingDetail.builder()
            .packagingType(request.getPackagingType())
            .details(request.getDetails())
            .build();
    }

}
