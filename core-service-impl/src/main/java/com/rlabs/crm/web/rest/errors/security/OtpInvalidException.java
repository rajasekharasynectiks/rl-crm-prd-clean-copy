package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class OtpInvalidException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("otp.invalid");
    public OtpInvalidException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
