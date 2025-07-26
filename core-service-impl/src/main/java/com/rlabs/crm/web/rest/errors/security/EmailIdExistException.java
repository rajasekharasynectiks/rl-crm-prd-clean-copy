package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class EmailIdExistException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("email.already.exist");
    public EmailIdExistException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
