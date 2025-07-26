package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class MFATokenNotProvidedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("mfa-token.not.provided");
    public MFATokenNotProvidedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
