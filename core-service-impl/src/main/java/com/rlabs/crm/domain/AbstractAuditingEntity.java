package com.rlabs.crm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract T getId();

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 255, updatable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false, columnDefinition = "TIMESTAMP")
    protected LocalDateTime createdOn;// = LocalDateTime.now(ZoneId.of(Constants.DEFAULT_TIMEZONE));;

    @LastModifiedBy
    @Column(name = "updated_by", length = 255)
    protected String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_on", columnDefinition = "TIMESTAMP")
    protected LocalDateTime updatedOn;// = LocalDateTime.now(ZoneId.of(Constants.DEFAULT_TIMEZONE));

}
