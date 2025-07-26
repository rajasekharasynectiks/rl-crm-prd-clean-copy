package com.rlabs.crm.defaultdata.errorcodes;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ErrorCodes implements Serializable {
    private int code;
    private String message;
    private String errorKey;
}
