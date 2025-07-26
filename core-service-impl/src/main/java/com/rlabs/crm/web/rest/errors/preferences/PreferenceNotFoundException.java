package com.rlabs.crm.web.rest.errors.preferences;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class PreferenceNotFoundException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("preference.not.found");
    public PreferenceNotFoundException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
