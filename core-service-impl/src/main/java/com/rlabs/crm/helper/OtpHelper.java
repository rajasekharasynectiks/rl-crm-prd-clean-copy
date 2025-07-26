package com.rlabs.crm.helper;


import com.rlabs.crm.payload.request.otp.OtpRequest;
import com.rlabs.crm.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class OtpHelper {

    @Autowired
    private DateTimeUtil dateTimeUtil;

    private static final ConcurrentMap<String, OtpRequest> CACHE = new ConcurrentHashMap<>();

    public void put(String key, OtpRequest value){
        CACHE.put(key, value);
    }

    public OtpRequest get(String key){
        return CACHE.get(key);
    }

    public boolean containKey(String key){
        return CACHE.containsKey(key);
    }

    public OtpRequest remove(String key){
        return CACHE.remove(key);
    }

}
