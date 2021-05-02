package com.arjun.cowin.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {

    private static final ThreadLocal<ObjectMapper> objectMapper = ThreadLocal.withInitial(() -> {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        return objectMapper;
    });

    public static ObjectMapper getObjectMapper() {
        return objectMapper.get();
    }

    public static <T> T toObject(String src, Class<T> classType) {
        try {
            return getObjectMapper().readValue(src, classType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
