package com.ugc.card.model;

import java.util.Map;

public class Condition {
    public ConditionType type;
    public Map<String, Object> parameters;
    
    @Override
    public String toString() {
        return "Condition{" +
            "type=" + type +
            ", parameters=" + parameters +
            '}';
    }
} 