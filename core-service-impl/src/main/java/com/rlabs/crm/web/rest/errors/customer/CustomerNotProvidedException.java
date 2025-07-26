package com.rlabs.crm.web.rest.errors.customer;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class CustomerNotProvidedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("customer.not.provided");
    public CustomerNotProvidedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
