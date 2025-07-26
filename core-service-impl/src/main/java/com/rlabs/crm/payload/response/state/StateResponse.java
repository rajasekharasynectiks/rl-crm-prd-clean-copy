package com.rlabs.crm.payload.response.state;

import com.rlabs.crm.domain.State;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateResponse {
    private Long id;
    private String stateCode;
    private String stateName ;

    public static StateResponse buildResponse(State state){
        return StateResponse.builder()
            .id(state.getId())
            .stateCode(state.getStateCode())
            .stateName(state.getStateName())
            .build();
    }
}

