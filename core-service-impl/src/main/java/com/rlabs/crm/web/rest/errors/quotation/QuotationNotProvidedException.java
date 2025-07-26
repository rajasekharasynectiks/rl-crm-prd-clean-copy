package com.rlabs.crm.web.rest.errors.quotation;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class QuotationNotProvidedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("quotation.not.provided");
    public QuotationNotProvidedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
