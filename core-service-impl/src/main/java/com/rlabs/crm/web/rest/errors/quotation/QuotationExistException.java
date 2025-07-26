package com.rlabs.crm.web.rest.errors.quotation;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class QuotationExistException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("quotation.already.exist");
    public QuotationExistException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
