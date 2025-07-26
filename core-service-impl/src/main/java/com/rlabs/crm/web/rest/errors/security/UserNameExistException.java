package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class UserNameExistException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("username.already.exist");
    public UserNameExistException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
