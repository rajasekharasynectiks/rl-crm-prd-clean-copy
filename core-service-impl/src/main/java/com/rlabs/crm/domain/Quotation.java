package com.rlabs.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "quotations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Quotation extends AbstractAuditingEntity implements IGlobalSearch{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;
    private String qtNumber;
    private String qtOwner;
    private String status;
    private LocalDate qtExpiryDate;
    private LocalDate qtDate;

    @Column(name = "dynamic_fields", columnDefinition = "NVARCHAR(MAX)")
    private String dynamicFields;

    private Integer version;
    private String comments;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnoreProperties(value = { "quotations" }, allowSetters = true)
    private Customer customer;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @JsonIgnore // to stop infinite recursion between parent and child entities
    private Product product;

}
