package com.rlabs.crm.defaultdata.errorcodes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.rlabs.crm.util.MapperUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.util.List;

@Slf4j
@Configuration
public class ErrorCodeLoader {

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void loadJsonFile() {
        try{
            Resource res = resourceLoader.getResource("classpath:error-codes.json");
            JsonNode errorCodeJson = mapperUtil.toObject(new String(res.getContentAsByteArray()), JsonNode.class);
            List<ErrorCodes> list = mapperUtil.convertObject(errorCodeJson.get("errors"), new TypeReference<List<ErrorCodes>>(){});
            for(ErrorCodes errorCodes: list){
                log.info("error-key: {}, error-code: {}",errorCodes.getErrorKey(),errorCodes);
                ErrorCodeCache.put(errorCodes.getErrorKey(), errorCodes);
            }
            log.info("Error codes loaded");
        } catch (IOException e) {
            log.error("Loading error codes from error-codes.json failed. Exception: ",e);
            System.exit(1);
        }
    }
}
