package com.rlabs.crm.web.rest.errors.document;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class DocumentNotProvidedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("document.not.provided");
    public DocumentNotProvidedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
