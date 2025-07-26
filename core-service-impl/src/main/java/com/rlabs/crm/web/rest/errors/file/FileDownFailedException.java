package com.rlabs.crm.web.rest.errors.file;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class FileDownFailedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("file.download.failed");
    public FileDownFailedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
