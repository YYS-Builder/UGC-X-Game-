package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TargetingChainTest {
    private TargetingChain chain;
    private TargetingComponent effectComponent;
    private TargetingComponent typeComponent;
    private TargetingComponent valueComponent;

    @BeforeEach
    void setUp() {
        chain = new TargetingChain();
        effectComponent = new TargetingComponent(ComponentCategory.EFFECT, "deal");
        typeComponent = new TargetingComponent(ComponentCategory.TYPE, "minion");
        valueComponent = new TargetingComponent(ComponentCategory.VALUE, "3");
    }

    @Test
    void testAddComponent() {
        chain.addComponent(effectComponent);
        assertEquals(1, chain.size());
        assertTrue(chain.getComponents().contains(effectComponent));
    }

    @Test
    void testAddNullComponent() {
        assertThrows(IllegalArgumentException.class, () -> {
            chain.addComponent(null);
        });
    }

    @Test
    void testGetComponents() {
        chain.addComponent(effectComponent);
        chain.addComponent(typeComponent);
        
        List<TargetingComponent> components = chain.getComponents();
        assertEquals(2, components.size());
        assertTrue(components.contains(effectComponent));
        assertTrue(components.contains(typeComponent));
    }

    @Test
    void testHasComponentOfCategory() {
        chain.addComponent(effectComponent);
        
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.EFFECT));
        assertFalse(chain.hasComponentOfCategory(ComponentCategory.TYPE));
    }

    @Test
    void testGetComponentsByCategory() {
        chain.addComponent(effectComponent);
        chain.addComponent(typeComponent);
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "draw"));
        
        List<TargetingComponent> effectComponents = chain.getComponentsByCategory(ComponentCategory.EFFECT);
        assertEquals(2, effectComponents.size());
        assertTrue(effectComponents.stream().allMatch(c -> c.getCategory() == ComponentCategory.EFFECT));
    }

    @Test
    void testGetFirstComponentByCategory() {
        chain.addComponent(effectComponent);
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "draw"));
        
        TargetingComponent first = chain.getFirstComponentByCategory(ComponentCategory.EFFECT);
        assertEquals(effectComponent, first);
    }

    @Test
    void testGetFirstComponentByCategoryNotFound() {
        chain.addComponent(effectComponent);
        
        TargetingComponent notFound = chain.getFirstComponentByCategory(ComponentCategory.VALUE);
        assertNull(notFound);
    }

    @Test
    void testSize() {
        assertEquals(0, chain.size());
        
        chain.addComponent(effectComponent);
        assertEquals(1, chain.size());
        
        chain.addComponent(typeComponent);
        assertEquals(2, chain.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(chain.isEmpty());
        
        chain.addComponent(effectComponent);
        assertFalse(chain.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        TargetingChain chain1 = new TargetingChain();
        chain1.addComponent(effectComponent);
        chain1.addComponent(typeComponent);

        TargetingChain chain2 = new TargetingChain();
        chain2.addComponent(effectComponent);
        chain2.addComponent(typeComponent);

        TargetingChain chain3 = new TargetingChain();
        chain3.addComponent(effectComponent);
        chain3.addComponent(valueComponent);

        // Test equals
        assertEquals(chain1, chain2);
        assertNotEquals(chain1, chain3);
        assertNotEquals(chain1, null);
        assertNotEquals(chain1, new Object());

        // Test hashCode
        assertEquals(chain1.hashCode(), chain2.hashCode());
        assertNotEquals(chain1.hashCode(), chain3.hashCode());
    }

    @Test
    void testToString() {
        chain.addComponent(effectComponent);
        chain.addComponent(typeComponent);
        
        String str = chain.toString();
        assertTrue(str.contains("components"));
        assertTrue(str.contains(effectComponent.toString()));
        assertTrue(str.contains(typeComponent.toString()));
    }

    @Test
    void testComponentOrder() {
        chain.addComponent(effectComponent);
        chain.addComponent(typeComponent);
        chain.addComponent(valueComponent);
        
        List<TargetingComponent> components = chain.getComponents();
        assertEquals(effectComponent, components.get(0));
        assertEquals(typeComponent, components.get(1));
        assertEquals(valueComponent, components.get(2));
    }

    @Test
    void testGetComponentsDefensiveCopy() {
        chain.addComponent(effectComponent);
        
        List<TargetingComponent> components = chain.getComponents();
        components.add(typeComponent);
        
        assertEquals(1, chain.size());
        assertFalse(chain.hasComponentOfCategory(ComponentCategory.TYPE));
    }
} 