package com.rlabs.crm.web.rest.errors.file;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class FileNotFoundException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("file.not.found");
    public FileNotFoundException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
