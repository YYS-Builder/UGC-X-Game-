package com.ugc.card.model.targeting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TargetingComponentTest {

    @Test
    void testConstructorWithValidInput() {
        TargetingComponent component = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        assertEquals(ComponentCategory.EFFECT, component.getCategory());
        assertEquals("deal", component.getText());
    }

    @Test
    void testConstructorNullCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TargetingComponent(null, "deal");
        });
    }

    @Test
    void testConstructorNullText() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TargetingComponent(ComponentCategory.EFFECT, null);
        });
    }

    @Test
    void testTextNormalization() {
        TargetingComponent component = new TargetingComponent(ComponentCategory.EFFECT, "  DEAL  ");
        assertEquals("deal", component.getText());
    }

    @Test
    void testEqualsAndHashCode() {
        TargetingComponent component1 = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        TargetingComponent component2 = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        TargetingComponent component3 = new TargetingComponent(ComponentCategory.EFFECT, "draw");
        TargetingComponent component4 = new TargetingComponent(ComponentCategory.TYPE, "deal");

        // Test equals
        assertEquals(component1, component2);
        assertNotEquals(component1, component3);
        assertNotEquals(component1, component4);
        assertNotEquals(component1, null);
        assertNotEquals(component1, new Object());

        // Test hashCode
        assertEquals(component1.hashCode(), component2.hashCode());
        assertNotEquals(component1.hashCode(), component3.hashCode());
        assertNotEquals(component1.hashCode(), component4.hashCode());
    }

    @Test
    void testToString() {
        TargetingComponent component = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        String str = component.toString();
        
        assertTrue(str.contains("EFFECT"));
        assertTrue(str.contains("deal"));
        assertTrue(str.contains("category"));
        assertTrue(str.contains("text"));
    }

    @Test
    void testDifferentCategories() {
        TargetingComponent effect = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        TargetingComponent type = new TargetingComponent(ComponentCategory.TYPE, "minion");
        TargetingComponent value = new TargetingComponent(ComponentCategory.VALUE, "3");
        TargetingComponent direction = new TargetingComponent(ComponentCategory.DIRECTION, "target");

        assertEquals(ComponentCategory.EFFECT, effect.getCategory());
        assertEquals(ComponentCategory.TYPE, type.getCategory());
        assertEquals(ComponentCategory.VALUE, value.getCategory());
        assertEquals(ComponentCategory.DIRECTION, direction.getCategory());
    }

    @Test
    void testWhitespaceHandling() {
        TargetingComponent component1 = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        TargetingComponent component2 = new TargetingComponent(ComponentCategory.EFFECT, " deal ");
        TargetingComponent component3 = new TargetingComponent(ComponentCategory.EFFECT, "deal\t");
        TargetingComponent component4 = new TargetingComponent(ComponentCategory.EFFECT, "\tdeal\t");

        assertEquals(component1, component2);
        assertEquals(component1, component3);
        assertEquals(component1, component4);
    }

    @Test
    void testCaseInsensitivity() {
        TargetingComponent component1 = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        TargetingComponent component2 = new TargetingComponent(ComponentCategory.EFFECT, "DEAL");
        TargetingComponent component3 = new TargetingComponent(ComponentCategory.EFFECT, "Deal");
        TargetingComponent component4 = new TargetingComponent(ComponentCategory.EFFECT, "dEaL");

        assertEquals(component1, component2);
        assertEquals(component1, component3);
        assertEquals(component1, component4);
    }
} 