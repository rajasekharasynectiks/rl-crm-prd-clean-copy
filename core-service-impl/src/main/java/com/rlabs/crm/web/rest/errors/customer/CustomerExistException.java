package com.rlabs.crm.web.rest.errors.customer;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class CustomerExistException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("customer.already.exist");
    public CustomerExistException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
