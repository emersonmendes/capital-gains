package br.com.nu.capitalgain.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class JsonUtils {

    static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("Cannot instantiate this Utility class");
    }

    public static <T> List<T> readList(String json, TypeReference<List<T>> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not read json", e);
        }
    }

    public static void writeValue(PrintStream out, Object value) {
        try {
            mapper.writeValue(out, value);
        } catch (IOException e) {
            throw new RuntimeException("Could not write json", e);
        }
    }

    public static String[] splitJsonArrays(String jsonStr) {
        return jsonStr.split("(?<=])\\s*(?=\\[)", -1);
    }

}
