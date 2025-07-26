package com.rlabs.crm.payload.response.country;

import com.rlabs.crm.domain.Country;
import com.rlabs.crm.domain.State;
import com.rlabs.crm.payload.response.state.StateResponse;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponse {
    private Long id;
    private String name;
    private String isoTwoCharCode;
    private String isoThreeCharCode;
    private String currencyCode;
    private String currencySymbol;
    private String isdCode;
    private List<StateResponse> states;

    public static CountryResponse buildResponse(Country country){
        return CountryResponse.builder()
            .id(country.getId())
            .name(country.getName())
            .isoTwoCharCode(country.getIsoTwoCharCode())
            .isoThreeCharCode(country.getIsoThreeCharCode())
            .currencyCode(country.getCurrencyCode())
            .currencySymbol(country.getCurrencySymbol())
            .isdCode(country.getIsdCode())
            .states(country.getStates() != null ? country.getStates().stream().map(StateResponse::buildResponse).collect(Collectors.toList()) : null)
            .build();
    }
}
