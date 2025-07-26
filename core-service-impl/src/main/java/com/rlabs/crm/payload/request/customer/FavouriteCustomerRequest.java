package com.rlabs.crm.payload.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteCustomerRequest implements Serializable {
    private Long id;
    private Boolean isFavourite;
}
