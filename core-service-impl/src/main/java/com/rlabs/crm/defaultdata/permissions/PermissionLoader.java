package com.rlabs.crm.defaultdata.permissions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.rlabs.crm.util.MapperUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration("permissionLoader")
public class PermissionLoader {

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void loadFromJsonFile() {
        try{
            Resource res = resourceLoader.getResource("classpath:permissions.json");
            JsonNode errorCodeJson = mapperUtil.toObject(new String(res.getContentAsByteArray()), JsonNode.class);
            List<Permissions> list = mapperUtil.convertObject(errorCodeJson, new TypeReference<List<Permissions>>(){});
            for(Permissions obj: list){
                log.info("perm cat: {}, perm: {}",obj.getCategory(),obj.getPermission());
                PermissionCache.put(obj.getCategory(), obj.getPermission());
            }
            log.info("Permission loaded");
        } catch (IOException e) {
            log.error("Loading permissions from permissions.json failed. Exception: ",e);
            System.exit(1);
        }
    }
}
