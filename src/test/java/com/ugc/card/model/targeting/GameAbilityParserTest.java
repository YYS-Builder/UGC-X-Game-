package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class GameAbilityParserTest {
    private GameAbilityParser parser;

    @BeforeEach
    void setUp() {
        // Create a simple test implementation of GameAbilityParser
        parser = new GameAbilityParser() {
            @Override
            public TargetingChain parseAbility(String text) {
                TargetingChain chain = new TargetingChain();
                if (text != null && !text.isEmpty()) {
                    chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "test_effect"));
                }
                return chain;
            }

            @Override
            public boolean isValidChain(TargetingChain chain) {
                return chain != null && chain.hasComponentOfCategory(ComponentCategory.EFFECT);
            }

            @Override
            public String generateText(TargetingChain chain) {
                if (chain == null || chain.isEmpty()) {
                    return "";
                }
                return chain.getFirstComponentByCategory(ComponentCategory.EFFECT).getText();
            }

            @Override
            public Set<ComponentCategory> getValidCategories() {
                return new HashSet<>(Arrays.asList(
                    ComponentCategory.EFFECT,
                    ComponentCategory.TYPE,
                    ComponentCategory.VALUE
                ));
            }

            @Override
            public Set<String> getValidComponents(ComponentCategory category) {
                switch (category) {
                    case EFFECT:
                        return new HashSet<>(Arrays.asList("test_effect", "another_effect"));
                    case TYPE:
                        return new HashSet<>(Arrays.asList("test_type"));
                    case VALUE:
                        return new HashSet<>(Arrays.asList("1", "2", "3"));
                    default:
                        return Collections.emptySet();
                }
            }
        };
    }

    @Test
    void testParseAbilityWithValidInput() {
        TargetingChain chain = parser.parseAbility("test ability");
        assertNotNull(chain);
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.EFFECT));
    }

    @Test
    void testParseAbilityWithEmptyInput() {
        TargetingChain chain = parser.parseAbility("");
        assertNotNull(chain);
        assertTrue(chain.isEmpty());
    }

    @Test
    void testParseAbilityWithNullInput() {
        TargetingChain chain = parser.parseAbility(null);
        assertNotNull(chain);
        assertTrue(chain.isEmpty());
    }

    @Test
    void testIsValidChainWithValidChain() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "test_effect"));
        assertTrue(parser.isValidChain(chain));
    }

    @Test
    void testIsValidChainWithInvalidChain() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "test_type"));
        assertFalse(parser.isValidChain(chain));
    }

    @Test
    void testIsValidChainWithNullChain() {
        assertFalse(parser.isValidChain(null));
    }

    @Test
    void testGenerateTextWithValidChain() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "test_effect"));
        assertEquals("test_effect", parser.generateText(chain));
    }

    @Test
    void testGenerateTextWithEmptyChain() {
        assertEquals("", parser.generateText(new TargetingChain()));
    }

    @Test
    void testGenerateTextWithNullChain() {
        assertEquals("", parser.generateText(null));
    }

    @Test
    void testGetValidCategories() {
        Set<ComponentCategory> categories = parser.getValidCategories();
        assertNotNull(categories);
        assertTrue(categories.contains(ComponentCategory.EFFECT));
        assertTrue(categories.contains(ComponentCategory.TYPE));
        assertTrue(categories.contains(ComponentCategory.VALUE));
        assertFalse(categories.contains(ComponentCategory.DIRECTION));
    }

    @Test
    void testGetValidComponentsForEffect() {
        Set<String> components = parser.getValidComponents(ComponentCategory.EFFECT);
        assertNotNull(components);
        assertTrue(components.contains("test_effect"));
        assertTrue(components.contains("another_effect"));
    }

    @Test
    void testGetValidComponentsForType() {
        Set<String> components = parser.getValidComponents(ComponentCategory.TYPE);
        assertNotNull(components);
        assertTrue(components.contains("test_type"));
    }

    @Test
    void testGetValidComponentsForValue() {
        Set<String> components = parser.getValidComponents(ComponentCategory.VALUE);
        assertNotNull(components);
        assertTrue(components.contains("1"));
        assertTrue(components.contains("2"));
        assertTrue(components.contains("3"));
    }

    @Test
    void testGetValidComponentsForUnsupportedCategory() {
        Set<String> components = parser.getValidComponents(ComponentCategory.DIRECTION);
        assertNotNull(components);
        assertTrue(components.isEmpty());
    }
} 