package com.rlabs.crm.service.product;

import com.rlabs.crm.domain.ProductIngredient;
import com.rlabs.crm.payload.request.product.AddProductIngredientRequest;
import com.rlabs.crm.repository.ProductIngredientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ProductIngredientService {

    @Autowired
    private ProductIngredientRepository productIngredientRepository;

    public List<ProductIngredient> findAll(){
        return productIngredientRepository.findAll();
    }

    public List<ProductIngredient> findAllById(List<Long> ids){
        return productIngredientRepository.findAllById(ids);
    }

    public ProductIngredient findById(Long id){
        return productIngredientRepository.findById(id).orElse(null);
    }

    @Transactional
    public ProductIngredient delete(Long id){
        log.debug("Deleting product ingredient. ProductIngredient id: {}",id);
        ProductIngredient productIngredient = findById(id);
        productIngredientRepository.deleteById(id);
        log.debug("Product ingredient deleted");
        return productIngredient;
    }


    public List<ProductIngredient> deleteAll(List<Long> ids){
        List<ProductIngredient> productIngredientList = findAllById(ids);
        productIngredientRepository.deleteAllById(ids);
        return productIngredientList;
    }

    public List<ProductIngredient> addAllProductIngredients(List<ProductIngredient> productIngredientList){
        log.debug("Adding all product ingredients");
        return productIngredientRepository.saveAll(productIngredientList);
    }

    public ProductIngredient buildProductIngredient(AddProductIngredientRequest request){
        return ProductIngredient.builder()
            .rmId(request.getRmId())
            .activeIngredient(request.getActiveIngredient())
            .label(request.getLabel())
            .units(request.getUnits())
            .perDosage(request.getPerDosage())
            .qtyUnit(request.getQtyUnit())
            .pricePerUnit(request.getPricePerUnit())
            .cost(request.getCost())
            .build();
    }

}
