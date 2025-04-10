package com.ugc.card.model;

import java.util.Map;
import java.util.HashMap;

public class DesignPoints {
    private static final Map<TargetType, Integer> TARGET_POINTS = new HashMap<>();
    private static final Map<EffectType, Integer> EFFECT_POINTS = new HashMap<>();
    private static final Map<ConditionType, Integer> CONDITION_POINTS = new HashMap<>();
    private static final Map<StateChangeType, Integer> STATE_CHANGE_POINTS = new HashMap<>();
    
    static {
        // Target Type Points
        TARGET_POINTS.put(TargetType.CREATURE, 1);
        TARGET_POINTS.put(TargetType.PLAYER, 1);
        TARGET_POINTS.put(TargetType.CARD, 1);
        TARGET_POINTS.put(TargetType.LOCATION, 2);
        TARGET_POINTS.put(TargetType.STACK, 2);
        TARGET_POINTS.put(TargetType.HAND, 2);
        TARGET_POINTS.put(TargetType.LIBRARY, 3);
        TARGET_POINTS.put(TargetType.GRAVEYARD, 2);
        TARGET_POINTS.put(TargetType.BATTLEFIELD, 2);
        
        // Effect Type Points
        EFFECT_POINTS.put(EffectType.DAMAGE, 1);
        EFFECT_POINTS.put(EffectType.HEAL, 1);
        EFFECT_POINTS.put(EffectType.MOVE, 1);
        EFFECT_POINTS.put(EffectType.DESTROY, 2);
        EFFECT_POINTS.put(EffectType.MODIFY, 1);
        EFFECT_POINTS.put(EffectType.DRAW, 2);
        EFFECT_POINTS.put(EffectType.SEARCH, 3);
        EFFECT_POINTS.put(EffectType.COUNTER, 2);
        
        // Condition Type Points
        CONDITION_POINTS.put(ConditionType.CONTROL, 1);
        CONDITION_POINTS.put(ConditionType.POWER, 1);
        CONDITION_POINTS.put(ConditionType.TOUGHNESS, 1);
        CONDITION_POINTS.put(ConditionType.CARD_TYPE, 1);
        CONDITION_POINTS.put(ConditionType.ZONE, 1);
        CONDITION_POINTS.put(ConditionType.TRIGGER, 1);
    }
    
    public static int calculatePoints(TargetStructure structure) {
        int points = 0;
        
        // Base target points
        if (structure.targetType != null) {
            points += TARGET_POINTS.getOrDefault(structure.targetType, 0);
        }
        
        // Effect points
        if (structure.effect != null) {
            points += EFFECT_POINTS.getOrDefault(structure.effect.type, 0);
            // Add points for effect parameters
            if (structure.effect.parameters != null) {
                points += calculateParameterPoints(structure.effect.parameters);
            }
        }
        
        // Condition points
        if (structure.conditions != null) {
            for (Condition condition : structure.conditions) {
                points += CONDITION_POINTS.getOrDefault(condition.type, 0);
                if (condition.parameters != null) {
                    points += calculateParameterPoints(condition.parameters);
                }
            }
        }
        
        // State change points
        if (structure.stateChanges != null) {
            for (StateChange stateChange : structure.stateChanges) {
                points += STATE_CHANGE_POINTS.getOrDefault(stateChange.type, 0);
                if (stateChange.parameters != null) {
                    points += calculateParameterPoints(stateChange.parameters);
                }
            }
        }
        
        // Recursive points for chained structures
        if (structure.next != null) {
            points += calculatePoints(structure.next);
        }
        if (structure.alternative != null) {
            points += calculatePoints(structure.alternative);
        }
        
        return points;
    }
    
    private static int calculateParameterPoints(Map<String, Object> parameters) {
        int points = 0;
        for (Object value : parameters.values()) {
            if (value instanceof Integer) {
                points += (Integer) value;
            } else if (value instanceof Boolean) {
                points += ((Boolean) value) ? 1 : 0;
            } else if (value instanceof String) {
                points += 1; // Basic string parameter
            }
        }
        return points;
    }
} 