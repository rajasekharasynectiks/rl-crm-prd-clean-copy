package com.rlabs.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_ingredients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductIngredient extends AbstractAuditingEntity implements IGlobalSearch{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rmId;
    private String activeIngredient;
    private String label;
    private String units ;
    private String perDosage ;
    private String qtyUnit ;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerUnit ;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost ;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnoreProperties(value = { "ingredients" }, allowSetters = true)
    private Product product;
}
