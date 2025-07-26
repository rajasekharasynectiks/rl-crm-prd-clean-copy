package com.rlabs.crm.web.rest.errors.quotation;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class QuotationNotFoundException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("quotation.not.found");
    public QuotationNotFoundException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
