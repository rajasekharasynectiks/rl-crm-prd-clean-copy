package com.rlabs.crm.web.rest.errors.globalsearch;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class GlobalSearchFailedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("global.search.failed");
    public GlobalSearchFailedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
