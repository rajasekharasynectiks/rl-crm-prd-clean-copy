package com.rlabs.crm.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Document extends AbstractAuditingEntity implements IGlobalSearch{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uid;

    @Column(name = "source", columnDefinition = "NVARCHAR(MAX)")
    private String source;

    private String localFilePath;
    private String fileName;
    private String fileType;
    private String templateId;
    private String backupType;
    private String backupLocation;
    private String backupAccessUrl;
    private LocalDate backupOn;

}
