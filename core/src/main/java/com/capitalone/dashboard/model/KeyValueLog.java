package com.capitalone.dashboard.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class KeyValueLog {
    
    private static final char SEPERATOR = ' ';
    private static final char EQUALS = '=';
    private static final char QUOTE = '"';
    
    private StringBuilder builder = new StringBuilder();
    
    private Map<String, Object> attributes = new LinkedHashMap<>();
    
    public KeyValueLog with(String key, Object value) {
        attributes.put(key, value);
        return this;
    }
    
    @Override
    public String toString() {
        
        Set<String> keySet = attributes.keySet();
        for(String key : keySet) {
            builder.append(key).append(EQUALS).append(QUOTE).append(attributes.get(key)).append(QUOTE).append(SEPERATOR);
        }
        
        return builder.toString().trim();
    }

}
