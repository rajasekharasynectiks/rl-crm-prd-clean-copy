package com.rlabs.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Product extends AbstractAuditingEntity implements IGlobalSearch{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;
    private String name;
    private String custPrdNumber;
    private String category ;
    private String type ;
    private String capsuleSize ;
    private String packagingType ;
    private Integer countPerBottle ;
    private Integer batchSize ;
    private Integer dosagePerUnit ;

    @Column(precision = 10, scale = 2)
    private BigDecimal materialCost ;

    @Column(precision = 10, scale = 2)
    private BigDecimal processLoss ;

    @Column(precision = 10, scale = 2)
    private BigDecimal fillerCost ;

    @Column(precision = 10, scale = 2)
    private BigDecimal freightCharges ;

    @Column(precision = 10, scale = 2)
    private BigDecimal markUp ;

    @Column(precision = 10, scale = 2)
    private BigDecimal capsuleFillingCost ;

    @Column(precision = 10, scale = 2)
    private BigDecimal packagingCost ;

    @Column(precision = 10, scale = 2)
    private BigDecimal testingCost ;

    @Column(precision = 10, scale = 2)
    private BigDecimal stabilityCost ;

    @Column(precision = 10, scale = 2)
    private BigDecimal fulfillmentCost ;

    @Column(name = "dynamic_fields", columnDefinition = "NVARCHAR(MAX)")
    private String dynamicFields;

    @ToString.Exclude
    @OneToOne(mappedBy = "product")
    private Quotation quotation;

    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private List<ProductIngredient> ingredients;

    @ToString.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private List<ProductPackagingDetail> productPackagingDetails;
}
