package dev.misei.einfachml.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CopyUtil {

    private static <T> T deepCopy(T incoming) {
        try {
            var objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(incoming);
            return objectMapper.readValue(result, new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
