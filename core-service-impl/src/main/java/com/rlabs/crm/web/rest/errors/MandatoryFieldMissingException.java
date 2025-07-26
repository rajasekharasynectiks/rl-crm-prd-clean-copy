package com.rlabs.crm.web.rest.errors;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;

public class MandatoryFieldMissingException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("mandatory.field.missing");
    public MandatoryFieldMissingException(String entity, String field){
        super(errorCodes.getCode(), String.format("%s. entity: %s, property: %s",errorCodes.getMessage(), entity, field));
    }

}
