package core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonSyntaxException;
import org.junit.Test;

import java.util.Objects;

import static core.JsonUtils.*;
import static org.junit.Assert.*;

public class JsonUtilsTest {
    @Test
    public void testStrictParsingGson() {
        // Streams that start with the non-execute prefix, ")]}'\n".
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("){}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("]{}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("}{}"));
        // Streams that include multiple top-level values. With strict parsing, each stream must contain exactly one top-level value.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{}{}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{}[]null"));
        // Top-level values of any type. With strict parsing, the top-level value must be an object or an array.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict(""));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("null"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("Abracadabra"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("13"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("\"literal\""));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("[]"));
        // shouldn't parse multiple top level values
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{} {}"));
        // Numbers may be NaNs or infinities.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"number\": NaN}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"number\": Infinity}"));
        // End of line comments starting with // or # and ending with a newline character.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{//comment\n}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{#comment\n}"));
        // C-style comments starting with /* and ending with */. Such comments may not be nested.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{/*comment*/}"));
        // Names that are unquoted or 'single quoted'.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{a: 1}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{'a': 1}"));
        // Strings that are unquoted or 'single quoted'.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": str}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": ''}"));
        // Array elements separated by ; instead of ,.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": [1;2]}"));
        // Unnecessary array separators. These are interpreted as if null was the omitted value.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": [1,]}"));
        // Names and values separated by = or => instead of :.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\" = 13}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\" => 13}"));
        // Name/value pairs separated by ; instead of ,.
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": 1; \"b\": 2}"));

        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": }"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": ,}"));
        assertThrows(JsonSyntaxException.class, () -> parseJsonStrict("{\"a\": 0,}"));

        assertTrue(parseJsonStrict("{} ").entrySet().isEmpty());
        assertTrue(parseJsonStrict("{\"a\": null} \n \n").get("a").isJsonNull());
        assertEquals(0, parseJsonStrict("{\"a\": 0}").get("a").getAsInt());
        assertEquals("", parseJsonStrict("{\"a\": \"\"}").get("a").getAsString());
        assertEquals(0, parseJsonStrict("{\"a\": []}").get("a").getAsJsonArray().size());
    }

    @Test
    public void testStrictParsingJackson() {
        // Streams that start with the non-execute prefix, ")]}'\n".
        assertNull(parseJsonStrict("){}", ArrayNode.class));
        assertNull(parseJsonStrict("]{}", ArrayNode.class));
        assertNull(parseJsonStrict("}{}", ArrayNode.class));
        // Streams that include multiple top-level values. With strict parsing, each stream must contain exactly one top-level value.
        assertNull(parseJsonStrict("{}{}", ArrayNode.class));
        assertNull(parseJsonStrict("{}[]null", ArrayNode.class));
        // Top-level values of any type. With strict parsing, the top-level value must be an object or an array.
        assertNull(parseJsonStrict("", ArrayNode.class));
        assertNull(parseJsonStrict("null", ArrayNode.class));
        assertNull(parseJsonStrict("Abracadabra", ArrayNode.class));
        assertNull(parseJsonStrict("13", ArrayNode.class));
        assertNull(parseJsonStrict("\"literal\"", ArrayNode.class));
        assertNull(parseJsonStrict("{}", ArrayNode.class));
        // shouldn't parse multiple top level values
        assertNull(parseJsonStrict("[] []", ArrayNode.class));
        // Numbers may be NaNs or infinities.
        assertNull(parseJsonStrict("{\"number\": NaN}", ArrayNode.class));
        assertNull(parseJsonStrict("{\"number\": Infinity}", ArrayNode.class));
        // End of line comments starting with // or # and ending with a newline character.
        assertNull(parseJsonStrict("{//comment\n}", ArrayNode.class));
        assertNull(parseJsonStrict("{#comment\n}", ArrayNode.class));
        // C-style comments starting with /* and ending with */. Such comments may not be nested.
        assertNull(parseJsonStrict("{/*comment*/}", ArrayNode.class));
        // Names that are unquoted or 'single quoted'.
        assertNull(parseJsonStrict("{a: 1}", ArrayNode.class));
        assertNull(parseJsonStrict("{'a': 1}", ArrayNode.class));
        // Strings that are unquoted or 'single quoted'.
        assertNull(parseJsonStrict("{\"a\": str}", ArrayNode.class));
        assertNull(parseJsonStrict("{\"a\": ''}", ArrayNode.class));
        // Array elements separated by ; instead of ,.
        assertNull(parseJsonStrict("{\"a\": [1;2]}", ArrayNode.class));
        // Unnecessary array separators. These are interpreted as if null was the omitted value.
        assertNull(parseJsonStrict("{\"a\": [1,]}", ArrayNode.class));
        // Names and values separated by = or => instead of :.
        assertNull(parseJsonStrict("{\"a\" = 13}", ArrayNode.class));
        assertNull(parseJsonStrict("{\"a\" => 13}", ArrayNode.class));
        // Name/value pairs separated by ; instead of ,.
        assertNull(parseJsonStrict("{\"a\": 1; \"b\": 2}", ArrayNode.class));

        assertNull(parseJsonStrict("{\"a\": }", ArrayNode.class));
        assertNull(parseJsonStrict("{\"a\": ,}", ArrayNode.class));
        assertNull(parseJsonStrict("{\"a\": 0,}", ArrayNode.class));

        assertTrue(
                Objects.requireNonNull(
                        parseJsonStrict("[]", ArrayNode.class)
                ).isEmpty()
        );
        assertTrue(
                Objects.requireNonNull(
                        parseJsonStrict("{\"a\": null} \n \n", ObjectNode.class)
                ).get("a").isNull()
        );
        assertTrue(
                Objects.requireNonNull(
                        parseJsonStrict("{\"a\": 0}", ObjectNode.class)
                ).get("a").isInt()
        );
        assertEquals("\"\"", Objects.requireNonNull(parseJsonStrict("{\"a\": \"\"}", ObjectNode.class)).get("a").toString());
        assertTrue(
                Objects.requireNonNull(
                        parseJsonStrict("{\"a\": []}", ObjectNode.class)
                ).get("a").isArray())
        ;
    }

    @Test
    public void toPrettyJsonTest() {
        assertEquals("{\r\n " +
                        " \"a\" : 0" +
                        "\r\n}",
                toPrettyJson("{\"a\": 0}")
        );

        assertEquals("[ {\r\n " +
                        " \"a\" : 0,\r\n  " +
                        "\"b\" : null" +
                        "\r\n} ]",
                toPrettyJson("[{\"a\": 0, \"b\": null}]")
        );

        assertEquals("null", toPrettyJson("null"));

        assertNull(toPrettyJson("{\"a\": str}"));
    }

    @Test
    public void toPrettyGsonTest() {
        assertEquals("{\n " +
                        " \"a\": 0" +
                        "\n}",
                toPrettyGson("{\"a\": 0}")
        );

        assertEquals("[\n  " +
                        "{\n " +
                        "   \"a\": 0\n" +
                        "  }" +
                        "\n]",
                toPrettyGson("[{\"a\": 0, \"b\": null}]")
        );

        assertEquals("null", toPrettyGson("null"));

        assertThrows(JsonSyntaxException.class, () -> toPrettyGson("{\"a\": str}"));
    }

}