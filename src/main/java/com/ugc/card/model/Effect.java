package com.ugc.card.model;

import java.util.Map;

public class Effect {
    public EffectType type;
    public Map<String, Object> parameters;
    
    @Override
    public String toString() {
        return "Effect{" +
            "type=" + type +
            ", parameters=" + parameters +
            '}';
    }
} 