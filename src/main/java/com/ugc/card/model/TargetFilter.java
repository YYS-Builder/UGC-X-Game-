package com.ugc.card.model;

import java.util.Map;
import java.util.HashMap;

public class TargetFilter {
    public Map<String, Object> filters;
    
    public TargetFilter() {
        this.filters = new HashMap<>();
    }
    
    public void addFilter(String key, Object value) {
        filters.put(key, value);
    }
    
    @Override
    public String toString() {
        return "TargetFilter{" +
            "filters=" + filters +
            '}';
    }
} 