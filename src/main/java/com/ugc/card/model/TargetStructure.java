package com.ugc.card.model;

import java.util.List;
import java.util.Map;

public class TargetStructure {
    public TargetType targetType;
    public TargetFilter targetFilter;
    public Integer targetCount;
    
    public Effect effect;
    public Map<String, Object> effectParameters;
    
    public Timing timing;
    public List<Condition> conditions;
    
    public TargetStructure next;
    public TargetStructure alternative;
    
    public List<StateChange> stateChanges;
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TargetStructure{");
        if (targetType != null) sb.append("targetType=").append(targetType);
        if (targetFilter != null) sb.append(", targetFilter=").append(targetFilter);
        if (targetCount != null) sb.append(", targetCount=").append(targetCount);
        if (effect != null) sb.append(", effect=").append(effect);
        if (effectParameters != null) sb.append(", effectParameters=").append(effectParameters);
        if (timing != null) sb.append(", timing=").append(timing);
        if (conditions != null) sb.append(", conditions=").append(conditions);
        if (next != null) sb.append(", next=").append(next);
        if (alternative != null) sb.append(", alternative=").append(alternative);
        if (stateChanges != null) sb.append(", stateChanges=").append(stateChanges);
        sb.append("}");
        return sb.toString();
    }
} 