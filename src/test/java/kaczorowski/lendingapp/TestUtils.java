package kaczorowski.lendingapp;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import static lombok.AccessLevel.PRIVATE;
import static org.skyscreamer.jsonassert.JSONParser.parseJSON;

@NoArgsConstructor(access = PRIVATE)
class TestUtils {

    @SneakyThrows
    public static String json(String rawJson) {
        return parseJSON(rawJson).toString();
    }

}
