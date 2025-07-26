package com.rlabs.crm.defaultdata.permissions;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class PermissionCache {
    private static final ConcurrentMap<String, List<String>> CACHE = new ConcurrentHashMap<>();

    public static void put(String key, List<String> value){
        CACHE.put(key, value);
    }

    public static List<String> get(String key){
        return CACHE.get(key);
    }

    public static List<String> getKeys(){
        return CACHE.keySet().stream().toList();
    }
}
