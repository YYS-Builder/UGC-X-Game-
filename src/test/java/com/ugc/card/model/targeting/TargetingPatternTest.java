package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TargetingPatternTest {
    private TargetingPattern pattern;
    private Map<ComponentCategory, Set<String>> validComponents;

    @BeforeEach
    void setUp() {
        // Set up valid components
        validComponents = new HashMap<>();
        validComponents.put(ComponentCategory.EFFECT, new HashSet<>(Arrays.asList("deal", "draw")));
        validComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("minion", "spell")));
        validComponents.put(ComponentCategory.VALUE, new HashSet<>(Arrays.asList("1", "2", "3")));
        validComponents.put(ComponentCategory.DIRECTION, new HashSet<>(Arrays.asList("target", "all")));

        // Create pattern with required and optional categories
        pattern = new TargetingPattern(
            "test_pattern",
            Arrays.asList(ComponentCategory.EFFECT, ComponentCategory.TYPE),
            Arrays.asList(ComponentCategory.VALUE, ComponentCategory.DIRECTION),
            validComponents
        );
    }

    @Test
    void testMatchesValidChain() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "minion"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        
        assertTrue(pattern.matches(chain));
    }

    @Test
    void testMatchesValidChainWithOptionalComponents() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "minion"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "target"));
        
        assertTrue(pattern.matches(chain));
    }

    @Test
    void testDoesNotMatchMissingRequiredCategory() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        
        assertFalse(pattern.matches(chain));
    }

    @Test
    void testDoesNotMatchInvalidComponent() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "invalid_effect"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "minion"));
        
        assertFalse(pattern.matches(chain));
    }

    @Test
    void testDoesNotMatchUnexpectedCategory() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "minion"));
        chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "damaged"));
        
        assertFalse(pattern.matches(chain));
    }

    @Test
    void testMatchesNullChain() {
        assertFalse(pattern.matches(null));
    }

    @Test
    void testGetName() {
        assertEquals("test_pattern", pattern.getName());
    }

    @Test
    void testGetRequiredCategories() {
        List<ComponentCategory> required = pattern.getRequiredCategories();
        assertEquals(2, required.size());
        assertTrue(required.contains(ComponentCategory.EFFECT));
        assertTrue(required.contains(ComponentCategory.TYPE));
    }

    @Test
    void testGetOptionalCategories() {
        List<ComponentCategory> optional = pattern.getOptionalCategories();
        assertEquals(2, optional.size());
        assertTrue(optional.contains(ComponentCategory.VALUE));
        assertTrue(optional.contains(ComponentCategory.DIRECTION));
    }

    @Test
    void testGetValidComponents() {
        Map<ComponentCategory, Set<String>> components = pattern.getValidComponents();
        assertEquals(validComponents.size(), components.size());
        
        for (ComponentCategory category : validComponents.keySet()) {
            assertTrue(components.containsKey(category));
            assertEquals(validComponents.get(category), components.get(category));
        }
    }

    @Test
    void testEqualsAndHashCode() {
        TargetingPattern same = new TargetingPattern(
            "test_pattern",
            Arrays.asList(ComponentCategory.EFFECT, ComponentCategory.TYPE),
            Arrays.asList(ComponentCategory.VALUE, ComponentCategory.DIRECTION),
            validComponents
        );
        
        TargetingPattern different = new TargetingPattern(
            "different_pattern",
            Arrays.asList(ComponentCategory.EFFECT, ComponentCategory.TYPE),
            Arrays.asList(ComponentCategory.VALUE, ComponentCategory.DIRECTION),
            validComponents
        );
        
        assertEquals(pattern, same);
        assertEquals(pattern.hashCode(), same.hashCode());
        assertNotEquals(pattern, different);
        assertNotEquals(pattern.hashCode(), different.hashCode());
    }

    @Test
    void testToString() {
        String str = pattern.toString();
        assertTrue(str.contains("test_pattern"));
        assertTrue(str.contains("requiredCategories"));
        assertTrue(str.contains("optionalCategories"));
        assertTrue(str.contains("validComponents"));
    }
} 