package com.rlabs.crm.web.rest.errors.security;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class RoleExistException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("role.already.exist");
    public RoleExistException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
