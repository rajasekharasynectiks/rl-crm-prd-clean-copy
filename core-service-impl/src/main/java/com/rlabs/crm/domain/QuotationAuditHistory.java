package com.rlabs.crm.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations_audit_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationAuditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quotationId;
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
    private String auditComments;
    private String operation;

    private String submittedBy;
    private LocalDateTime submittedOn;

    @Column(name = "changes", columnDefinition = "NVARCHAR(MAX)")
    private String changes;

}
