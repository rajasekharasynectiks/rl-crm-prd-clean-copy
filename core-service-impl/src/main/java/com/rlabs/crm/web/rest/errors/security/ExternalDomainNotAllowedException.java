package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class ExternalDomainNotAllowedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("domain.not.allowed");
    public ExternalDomainNotAllowedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

    public ExternalDomainNotAllowedException(String msg){
        super(errorCodes.getCode(), errorCodes.getMessage()+". "+msg);
    }
}
