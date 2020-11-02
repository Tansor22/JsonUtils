package core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

public class JsonUtils {

    private static final TypeAdapter<JsonObject> strictGsonObjectAdapter =
            new Gson().getAdapter(JsonObject.class);

    public static JsonObject parseJsonStrict(String json) {
        try {
            try (JsonReader reader = new JsonReader(new StringReader(json))) {
                JsonObject result = strictGsonObjectAdapter.read(reader);
                reader.hasNext(); // throws on multiple top level values
                return result;
            }
        } catch (IOException e) {
            throw new JsonSyntaxException(e);
        }
    }

    public static <T extends JsonNode> T parseJsonStrict(String json, Class<T> jsonClass) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return jsonClass.cast(mapper.readTree(json));
        } catch (IOException | ClassCastException e) {
            return null;
        }
    }

    public static String toPrettyGson(String json) {
        try (JsonReader reader = new JsonReader(new StringReader(json))) {
            JsonElement jsonObject = new Gson().getAdapter(JsonElement.class).read(reader);
            return new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
        } catch (IOException e) {
            throw new JsonSyntaxException(e);
        }
    }

    public static String toPrettyJson(String json) {
        JsonNode jsonNode = parseJsonStrict(json, JsonNode.class);
        return jsonNode != null ? jsonNode.toPrettyString() : null;
    }

}

