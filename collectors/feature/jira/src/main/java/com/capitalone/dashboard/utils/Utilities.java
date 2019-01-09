package com.capitalone.dashboard.utils;

import org.json.simple.JSONObject;

public class Utilities {
    public static String getString(JSONObject json, String key) {
        if (json == null) return "";
        Object o = json.get(key);
        if (o == null) return "";
        if (o instanceof Double) {
            Double d = (Double) o;
            return String.valueOf(d.intValue());
        }
        return String.valueOf(o);
    }

    public static long getLong(JSONObject json, String key) {
        if (json == null) return 0;
        Object o = json.get(key);
        if (o == null) return 0;
        return (Long) o;
    }
}
