package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class EnforceMFAEnableException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("Enforced.MFA.enable.failed");
    public EnforceMFAEnableException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
