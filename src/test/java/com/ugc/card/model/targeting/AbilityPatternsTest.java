package com.ugc.card.model.targeting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class AbilityPatternsTest {
    
    @Test
    void testMatchesPattern() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "3"));
        chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "target"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "creature"));
        
        assertTrue(AbilityPatterns.matchesPattern(chain, "damage"));
        assertFalse(AbilityPatterns.matchesPattern(chain, "draw"));
    }
    
    @Test
    void testGetPattern() {
        List<ComponentCategory> pattern = AbilityPatterns.getPattern("damage");
        assertNotNull(pattern);
        assertEquals(4, pattern.size());
        assertTrue(pattern.contains(ComponentCategory.EFFECT));
        assertTrue(pattern.contains(ComponentCategory.VALUE));
        assertTrue(pattern.contains(ComponentCategory.DIRECTION));
        assertTrue(pattern.contains(ComponentCategory.TYPE));
    }
    
    @Test
    void testGetAvailablePatterns() {
        Set<String> patterns = AbilityPatterns.getAvailablePatterns();
        assertTrue(patterns.contains("damage"));
        assertTrue(patterns.contains("draw"));
        assertTrue(patterns.contains("gain_life"));
        assertTrue(patterns.contains("destroy"));
        assertTrue(patterns.contains("counter"));
        assertTrue(patterns.contains("trigger"));
        assertTrue(patterns.contains("condition"));
    }
    
    @Test
    void testIsValidPattern() {
        assertTrue(AbilityPatterns.isValidPattern("damage"));
        assertTrue(AbilityPatterns.isValidPattern("draw"));
        assertFalse(AbilityPatterns.isValidPattern("invalid_pattern"));
    }
    
    @Test
    void testCreatePatternChain() {
        TargetingChain chain = AbilityPatterns.createPatternChain("damage");
        assertNotNull(chain);
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.EFFECT));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.VALUE));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.DIRECTION));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.TYPE));
        
        // Test invalid pattern
        assertNull(AbilityPatterns.createPatternChain("invalid_pattern"));
    }
    
    @Test
    void testGetCommonValues() {
        Map<String, List<String>> values = AbilityPatterns.getCommonValues();
        
        // Test damage values
        List<String> damageValues = values.get("damage");
        assertNotNull(damageValues);
        assertTrue(damageValues.contains("1"));
        assertTrue(damageValues.contains("3"));
        assertTrue(damageValues.contains("5"));
        
        // Test direction values
        List<String> directionValues = values.get("direction");
        assertNotNull(directionValues);
        assertTrue(directionValues.contains("target"));
        assertTrue(directionValues.contains("each"));
        assertTrue(directionValues.contains("all"));
        
        // Test state values
        List<String> stateValues = values.get("state");
        assertNotNull(stateValues);
        assertTrue(stateValues.contains("tapped"));
        assertTrue(stateValues.contains("attacking"));
        assertTrue(stateValues.contains("blocking"));
    }
} 