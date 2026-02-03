package org.example.constants;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    public static String X_API_KEY = "qazWSXedc";

    public static Map<String, String> getX_API_KEY() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-Key", "qazWSXedc");
        return headers;
    }

    public static Map<String, String> getWrongX_API_KEY() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-Key", "qazWSXedQ");
        return headers;
    }

    public static Map<String, String> getEmptyX_API_KEY() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-Key", "");
        return headers;
    }

}
