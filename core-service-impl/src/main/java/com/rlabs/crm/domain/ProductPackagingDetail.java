package com.rlabs.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_packaging_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProductPackagingDetail extends AbstractAuditingEntity implements IGlobalSearch{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packagingType;

    @Column(name = "details", columnDefinition = "NVARCHAR(MAX)")
    private String details;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnoreProperties(value = { "productPackagingDetails" }, allowSetters = true)
    private Product product;
}
