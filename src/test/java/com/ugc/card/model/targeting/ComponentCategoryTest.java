package com.ugc.card.model.targeting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentCategoryTest {

    @Test
    void testEnumValues() {
        ComponentCategory[] categories = ComponentCategory.values();
        
        // Test core categories
        assertTrue(containsCategory(categories, ComponentCategory.TYPE));
        assertTrue(containsCategory(categories, ComponentCategory.EFFECT));
        assertTrue(containsCategory(categories, ComponentCategory.DIRECTION));
        assertTrue(containsCategory(categories, ComponentCategory.VALUE));
        assertTrue(containsCategory(categories, ComponentCategory.STATE));
        assertTrue(containsCategory(categories, ComponentCategory.TARGET));
        
        // Test modifier and condition categories
        assertTrue(containsCategory(categories, ComponentCategory.MODIFIER));
        assertTrue(containsCategory(categories, ComponentCategory.CONDITION));
        assertTrue(containsCategory(categories, ComponentCategory.TRIGGER));
        assertTrue(containsCategory(categories, ComponentCategory.TIMING));
        
        // Test resource related categories
        assertTrue(containsCategory(categories, ComponentCategory.COST));
        assertTrue(containsCategory(categories, ComponentCategory.RESOURCE));
        
        // Test game zone categories
        assertTrue(containsCategory(categories, ComponentCategory.ZONE));
        assertTrue(containsCategory(categories, ComponentCategory.LOCATION));
        
        // Test ownership and control categories
        assertTrue(containsCategory(categories, ComponentCategory.OWNER));
        assertTrue(containsCategory(categories, ComponentCategory.CONTROL));
        
        // Test special categories
        assertTrue(containsCategory(categories, ComponentCategory.KEYWORD));
        assertTrue(containsCategory(categories, ComponentCategory.COUNTER));
        assertTrue(containsCategory(categories, ComponentCategory.TAG));
        assertTrue(containsCategory(categories, ComponentCategory.SPECIAL));
    }

    @Test
    void testEnumValueOf() {
        assertEquals(ComponentCategory.TYPE, ComponentCategory.valueOf("TYPE"));
        assertEquals(ComponentCategory.EFFECT, ComponentCategory.valueOf("EFFECT"));
        assertEquals(ComponentCategory.DIRECTION, ComponentCategory.valueOf("DIRECTION"));
        assertEquals(ComponentCategory.VALUE, ComponentCategory.valueOf("VALUE"));
        assertEquals(ComponentCategory.STATE, ComponentCategory.valueOf("STATE"));
    }

    @Test
    void testEnumOrdinal() {
        // Test that ordinals are unique
        ComponentCategory[] categories = ComponentCategory.values();
        for (int i = 0; i < categories.length; i++) {
            for (int j = i + 1; j < categories.length; j++) {
                assertNotEquals(categories[i].ordinal(), categories[j].ordinal());
            }
        }
    }

    @Test
    void testEnumToString() {
        assertEquals("TYPE", ComponentCategory.TYPE.toString());
        assertEquals("EFFECT", ComponentCategory.EFFECT.toString());
        assertEquals("DIRECTION", ComponentCategory.DIRECTION.toString());
        assertEquals("VALUE", ComponentCategory.VALUE.toString());
        assertEquals("STATE", ComponentCategory.STATE.toString());
    }

    @Test
    void testInvalidEnumValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            ComponentCategory.valueOf("INVALID_CATEGORY");
        });
    }

    private boolean containsCategory(ComponentCategory[] categories, ComponentCategory category) {
        for (ComponentCategory c : categories) {
            if (c == category) {
                return true;
            }
        }
        return false;
    }
} 