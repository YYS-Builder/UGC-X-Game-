package com.ugc.card.model.targeting;

import java.util.*;

public class AbilityPatterns {
    private static final Map<String, List<ComponentCategory>> COMMON_PATTERNS = new HashMap<>();
    
    static {
        // Initialize common patterns
        
        // Deal damage pattern: [Effect, Value, Direction, Type]
        COMMON_PATTERNS.put("damage", Arrays.asList(
            ComponentCategory.EFFECT,
            ComponentCategory.VALUE,
            ComponentCategory.DIRECTION,
            ComponentCategory.TYPE
        ));
        
        // Draw cards pattern: [Effect, Value]
        COMMON_PATTERNS.put("draw", Arrays.asList(
            ComponentCategory.EFFECT,
            ComponentCategory.VALUE
        ));
        
        // Gain life pattern: [Effect, Value]
        COMMON_PATTERNS.put("gain_life", Arrays.asList(
            ComponentCategory.EFFECT,
            ComponentCategory.VALUE
        ));
        
        // Destroy target pattern: [Effect, Direction, Type]
        COMMON_PATTERNS.put("destroy", Arrays.asList(
            ComponentCategory.EFFECT,
            ComponentCategory.DIRECTION,
            ComponentCategory.TYPE
        ));
        
        // Counter spell pattern: [Effect, Direction, Type]
        COMMON_PATTERNS.put("counter", Arrays.asList(
            ComponentCategory.EFFECT,
            ComponentCategory.DIRECTION,
            ComponentCategory.TYPE
        ));
        
        // Trigger pattern: [Trigger, Type, State/Effect]
        COMMON_PATTERNS.put("trigger", Arrays.asList(
            ComponentCategory.TRIGGER,
            ComponentCategory.TYPE,
            ComponentCategory.STATE,
            ComponentCategory.EFFECT
        ));
        
        // Condition pattern: [Condition, Direction, Type, State, Effect]
        COMMON_PATTERNS.put("condition", Arrays.asList(
            ComponentCategory.CONDITION,
            ComponentCategory.DIRECTION,
            ComponentCategory.TYPE,
            ComponentCategory.STATE,
            ComponentCategory.EFFECT
        ));
    }
    
    public static boolean matchesPattern(TargetingChain chain, String patternName) {
        List<ComponentCategory> pattern = COMMON_PATTERNS.get(patternName);
        if (pattern == null) {
            return false;
        }
        
        // Check if chain contains all required categories in the pattern
        for (ComponentCategory category : pattern) {
            if (!chain.hasComponentOfCategory(category)) {
                return false;
            }
        }
        
        return true;
    }
    
    public static List<ComponentCategory> getPattern(String patternName) {
        return COMMON_PATTERNS.getOrDefault(patternName, Collections.emptyList());
    }
    
    public static Set<String> getAvailablePatterns() {
        return COMMON_PATTERNS.keySet();
    }
    
    public static boolean isValidPattern(String patternName) {
        return COMMON_PATTERNS.containsKey(patternName);
    }
    
    public static TargetingChain createPatternChain(String patternName) {
        List<ComponentCategory> pattern = COMMON_PATTERNS.get(patternName);
        if (pattern == null) {
            return null;
        }
        
        TargetingChain chain = new TargetingChain();
        for (ComponentCategory category : pattern) {
            // Add placeholder components
            chain.addComponent(new TargetingComponent(category, ""));
        }
        
        return chain;
    }
    
    public static Map<String, List<String>> getCommonValues() {
        Map<String, List<String>> values = new HashMap<>();
        
        values.put("damage", Arrays.asList("1", "2", "3", "4", "5"));
        values.put("draw", Arrays.asList("1", "2", "3"));
        values.put("life", Arrays.asList("1", "2", "3", "4", "5"));
        values.put("creature", Arrays.asList("creature", "artifact", "enchantment"));
        values.put("direction", Arrays.asList("target", "each", "all", "any"));
        values.put("state", Arrays.asList("tapped", "untapped", "attacking", "blocking"));
        values.put("trigger", Arrays.asList("when", "whenever", "at"));
        values.put("condition", Arrays.asList("if", "unless"));
        
        return values;
    }
} 