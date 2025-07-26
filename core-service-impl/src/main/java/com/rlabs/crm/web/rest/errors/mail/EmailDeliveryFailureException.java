package com.rlabs.crm.web.rest.errors.mail;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class EmailDeliveryFailureException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("email.delivery.failed");
    public EmailDeliveryFailureException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
