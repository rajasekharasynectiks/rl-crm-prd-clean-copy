package com.rlabs.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Customer extends AbstractAuditingEntity implements IGlobalSearch{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;
    private String name;
    private String companyName;
    private String companyAbrv;
    private String phoneNo;
    private String email;
    private String address;
    private String city;
    private String zipCode;
    private String country;
    private String state;
    private Boolean favourite;
    private Boolean isNew;
    private Boolean isLead;
    private String status;
    private String profileImageLocation; // local/s3/mongodb/sqldb etc..
    private String profileImageAccessUri; // localpath/s3 bucket/mongodb id/sqldb id etc..
    private String profileImageFileName;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private List<Quotation> quotations;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private List<CustomerNote> notes;
}
