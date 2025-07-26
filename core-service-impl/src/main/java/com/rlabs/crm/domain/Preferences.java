package com.rlabs.crm.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Preferences extends AbstractAuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String pfKey ;
    private String pfValue ;

}
