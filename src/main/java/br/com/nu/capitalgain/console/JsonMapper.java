package br.com.nu.capitalgain.console;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class JsonMapper {

    final ObjectMapper mapper;

    public JsonMapper(){
        this.mapper = new ObjectMapper();
        this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    public <T> List<T> readList(String json, TypeReference<List<T>> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not read json", e);
        }
    }

    public void writeValue(PrintStream out, Object value) {
        try {
            this.mapper.writeValue(out, value);
        } catch (IOException e) {
            throw new RuntimeException("Could not write json", e);
        }
    }

}
