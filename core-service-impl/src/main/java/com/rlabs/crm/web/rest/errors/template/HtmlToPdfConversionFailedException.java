package com.rlabs.crm.web.rest.errors.template;

import com.rlabs.crm.defaultdata.errorcodes.ErrorCodeCache;
import com.rlabs.crm.defaultdata.errorcodes.ErrorCodes;
import com.rlabs.crm.web.rest.errors.RlCrmException;

public class HtmlToPdfConversionFailedException extends RlCrmException {

    private static ErrorCodes errorCodes = ErrorCodeCache.get("html.to.pdf.conversion.failed");
    public HtmlToPdfConversionFailedException(){
        super(errorCodes.getCode(), errorCodes.getMessage());
    }

}
