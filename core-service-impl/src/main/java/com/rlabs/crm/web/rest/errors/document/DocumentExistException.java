package com.rlabs.crm.web.rest.errors.document;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class DocumentExistException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("document.already.exist");
    public DocumentExistException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
