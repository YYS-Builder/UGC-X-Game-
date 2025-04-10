package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbilityParserTest {
    private AbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new AbilityParser();
    }
    
    @Test
    void testParseSimpleAbility() {
        String ability = "deal 3 damage to target creature";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.EFFECT));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.VALUE));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.DIRECTION));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.TYPE));
        
        assertEquals("deal", chain.getFirstComponentByCategory(ComponentCategory.EFFECT).getText());
        assertEquals("3", chain.getFirstComponentByCategory(ComponentCategory.VALUE).getText());
        assertEquals("target", chain.getFirstComponentByCategory(ComponentCategory.DIRECTION).getText());
        assertEquals("creature", chain.getFirstComponentByCategory(ComponentCategory.TYPE).getText());
    }
    
    @Test
    void testParseTriggerAbility() {
        String ability = "whenever a creature attacks, draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.TRIGGER));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.TYPE));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.STATE));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.EFFECT));
        
        assertEquals("whenever", chain.getFirstComponentByCategory(ComponentCategory.TRIGGER).getText());
        assertEquals("creature", chain.getFirstComponentByCategory(ComponentCategory.TYPE).getText());
        assertEquals("attacking", chain.getFirstComponentByCategory(ComponentCategory.STATE).getText());
        assertEquals("draw", chain.getFirstComponentByCategory(ComponentCategory.EFFECT).getText());
    }
    
    @Test
    void testParseConditionalAbility() {
        String ability = "if target creature is tapped, destroy it";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.CONDITION));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.DIRECTION));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.TYPE));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.STATE));
        assertTrue(chain.hasComponentOfCategory(ComponentCategory.EFFECT));
        
        assertEquals("if", chain.getFirstComponentByCategory(ComponentCategory.CONDITION).getText());
        assertEquals("target", chain.getFirstComponentByCategory(ComponentCategory.DIRECTION).getText());
        assertEquals("creature", chain.getFirstComponentByCategory(ComponentCategory.TYPE).getText());
        assertEquals("tapped", chain.getFirstComponentByCategory(ComponentCategory.STATE).getText());
        assertEquals("destroy", chain.getFirstComponentByCategory(ComponentCategory.EFFECT).getText());
    }
    
    @Test
    void testValidChain() {
        String ability = "deal 3 damage to target creature";
        TargetingChain chain = parser.parseAbility(ability);
        assertTrue(parser.isValidChain(chain));
    }
    
    @Test
    void testInvalidChain() {
        String ability = "target creature";  // No effect
        TargetingChain chain = parser.parseAbility(ability);
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testGenerateText() {
        String ability = "deal 3 damage to target creature";
        TargetingChain chain = parser.parseAbility(ability);
        String generated = parser.generateText(chain);
        
        // Note: The generated text might not match exactly due to word order,
        // but should contain all the components
        assertTrue(generated.contains("deal"));
        assertTrue(generated.contains("3"));
        assertTrue(generated.contains("target"));
        assertTrue(generated.contains("creature"));
    }
} 