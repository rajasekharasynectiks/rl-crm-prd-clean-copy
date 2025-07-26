package com.rlabs.crm.web.rest.errors;

public class RlCrmException extends RuntimeException {

    protected int code;
    protected String message;
    public RlCrmException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
