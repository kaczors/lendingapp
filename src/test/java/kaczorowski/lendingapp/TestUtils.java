package kaczorowski.lendingapp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONParser;

import static lombok.AccessLevel.PRIVATE;
import static org.skyscreamer.jsonassert.JSONParser.parseJSON;

@NoArgsConstructor(access = PRIVATE)
class TestUtils {

    @SneakyThrows
    public static String json(String rawJson) {
        return parseJSON(rawJson).toString();
    }

}
