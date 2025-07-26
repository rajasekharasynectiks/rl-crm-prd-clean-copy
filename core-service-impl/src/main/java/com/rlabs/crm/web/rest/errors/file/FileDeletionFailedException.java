package com.rlabs.crm.web.rest.errors.file;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class FileDeletionFailedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("file.deletion.failed");
    public FileDeletionFailedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
