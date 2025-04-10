package com.ugc.card.model;

import java.util.Map;

public class StateChange {
    public StateChangeType type;
    public Map<String, Object> parameters;
    
    @Override
    public String toString() {
        return "StateChange{" +
            "type=" + type +
            ", parameters=" + parameters +
            '}';
    }
} 