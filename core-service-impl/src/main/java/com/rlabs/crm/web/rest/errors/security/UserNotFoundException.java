package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class UserNotFoundException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("user.not.found");
    public UserNotFoundException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
