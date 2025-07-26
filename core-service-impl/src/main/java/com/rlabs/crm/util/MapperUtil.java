package com.rlabs.crm.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Slf4j
@Component
public class MapperUtil {

    private final ObjectMapper mapper;

    public ObjectMapper getMapper() {
        return mapper;
    }

    public MapperUtil(ObjectMapper mapper) {
        this.mapper = mapper;
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
            @Override
            public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException, JsonProcessingException {
                String value = jsonParser.getText();
                return parseDateTimeString(value);
            }
        });
        mapper.registerModule(module);
    }

    private DateTimeFormatter dateTimeFormater = new DateTimeFormatterBuilder()
        .appendOptional(DateTimeFormatter.ISO_DATE_TIME).appendOptional(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .appendOptional(DateTimeFormatter.ISO_INSTANT)
        .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SX"))
        .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX"))
        .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toFormatter().withZone(ZoneOffset.UTC);

    public OffsetDateTime parseDateTimeString(String str) {
        return ZonedDateTime.from(dateTimeFormater.parse(str)).toOffsetDateTime();
    }

    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Parsing error ", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T toObject(String data, Class<T> type) {
        try {
            return mapper.readValue(data, type);
        } catch (Exception e) {
            log.error("Parsing error ", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T toObject(String data, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(data, typeReference);
        } catch (Exception e) {
            log.error("Parsing error ", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T convertObject(Object data, Class<T> type) {
        try {
            return mapper.convertValue(data, type);
        } catch (Exception e) {
            log.error("Parsing error ", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T convertObject(Object data, TypeReference<T> typeReference) {
        try {
            return mapper.convertValue(data, typeReference);
        } catch (Exception e) {
            log.error("Parsing error ", e);
            throw new RuntimeException(e);
        }
    }

    public JsonNode getTree(String object){
        try {
            return mapper.readTree(object);
        } catch (JsonProcessingException e) {
            log.error("Parsing error ", e);
            throw new RuntimeException(e);
        }
    }
}
