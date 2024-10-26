package pl.pollub.frontend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtil {
    public static final Gson GSON = new Gson();

    public static String toJson(Object object) {
        if(object instanceof String stringObject)
            return stringObject;

        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonElement fromJson(String json) {
        return JsonParser.parseString(json);
    }
}
