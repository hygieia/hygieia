package com.capitalone.dashboard.util;

import java.util.HashMap;
import java.util.Map;

public class WidgetOptionsBuilder {
    private Map<String, Object> options = new HashMap<String, Object>();

    public Map<String, Object> get() {
        return options;
    }

    public WidgetOptionsBuilder put(String key, Object value) {
        options.put(key, value);
        return this;
    }
}
