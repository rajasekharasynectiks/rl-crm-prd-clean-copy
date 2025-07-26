package com.rlabs.crm.defaultdata.errorcodes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ErrorCodeCache {
    private static final ConcurrentMap<String, ErrorCodes> CACHE = new ConcurrentHashMap<>();

    public static void put(String key, ErrorCodes value){
        CACHE.put(key, value);
    }

    public static ErrorCodes get(String key){
        return CACHE.get(key);
    }
}
