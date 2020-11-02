import com.google.gson.JsonObject;
import core.JsonUtils;

public class Runner {
    public static void main(String[] args) {
        String jsonString = "{" +
                    "\"hello\" : \"World!\"" +
                "}";
        JsonObject jsonObject = JsonUtils.parseJsonStrict(jsonString);
        System.out.println("Json object is \n" + jsonObject);
    }
}
