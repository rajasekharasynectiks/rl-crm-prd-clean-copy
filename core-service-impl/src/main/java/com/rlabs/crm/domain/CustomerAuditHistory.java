package com.rlabs.crm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers_audit_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAuditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
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
    private String submittedBy;
    private LocalDateTime submittedOn;
    private String operation;
    @Column(name = "changes", columnDefinition = "NVARCHAR(MAX)")
    private String changes;

}
