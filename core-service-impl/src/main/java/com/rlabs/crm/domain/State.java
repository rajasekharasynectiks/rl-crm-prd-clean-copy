package com.rlabs.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stateCode;
    private String stateName ;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnoreProperties(value = { "states" }, allowSetters = true)
    private Country country;


}
