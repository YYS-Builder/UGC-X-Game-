package com.ugc.card.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbilityParserTest {
    private final AbilityParser parser = new AbilityParser();
    
    @Test
    public void testSimpleDamage() {
        String ability = "Deal 3 damage to target creature";
        TargetStructure structure = parser.parseAbility(ability);
        
        assertNotNull(structure);
        assertEquals(TargetType.CREATURE, structure.targetType);
        assertEquals(EffectType.DAMAGE, structure.effect.type);
        assertEquals(3, structure.effect.parameters.get("amount"));
        
        // Test text generation
        assertEquals("Deal 3 damage to target creature", 
            AbilityGenerator.generateAbility(structure));
    }
    
    @Test
    public void testComplexChain() {
        String ability = "Destroy target creature, then if that creature was destroyed this way, draw a card";
        TargetStructure structure = parser.parseAbility(ability);
        
        assertNotNull(structure);
        assertEquals(TargetType.CREATURE, structure.targetType);
        assertEquals(EffectType.DESTROY, structure.effect.type);
        
        assertNotNull(structure.next);
        assertEquals(1, structure.next.conditions.size());
        assertEquals(EffectType.DRAW, structure.next.effect.type);
        assertEquals(1, structure.next.effect.parameters.get("amount"));
        
        // Test text generation
        assertEquals("Destroy target creature, then if that creature was destroyed this way, draw 1 card", 
            AbilityGenerator.generateAbility(structure));
    }
    
    @Test
    public void testLocationBased() {
        String ability = "Move all enemy 3 and 4-Cost cards to this location";
        TargetStructure structure = parser.parseAbility(ability);
        
        assertNotNull(structure);
        assertEquals(TargetType.CARD, structure.targetType);
        assertNotNull(structure.targetFilter);
        assertEquals(EffectType.MOVE, structure.effect.type);
        
        // Test text generation
        assertEquals("Move all enemy 3 and 4-Cost cards to this location", 
            AbilityGenerator.generateAbility(structure));
    }
    
    @Test
    public void testConditionalEffect() {
        String ability = "If you control a creature, draw a card";
        TargetStructure structure = parser.parseAbility(ability);
        
        assertNotNull(structure);
        assertEquals(1, structure.conditions.size());
        assertEquals(ConditionType.CONTROL, structure.conditions.get(0).type);
        assertEquals(EffectType.DRAW, structure.effect.type);
        
        // Test text generation
        assertEquals("if you control a creature, draw 1 card", 
            AbilityGenerator.generateAbility(structure));
    }
    
    @Test
    public void testMultiTarget() {
        String ability = "Deal 2 damage to any number of target creatures and/or players";
        TargetStructure structure = parser.parseAbility(ability);
        
        assertNotNull(structure);
        assertEquals(TargetType.CREATURE, structure.targetType);
        assertEquals(EffectType.DAMAGE, structure.effect.type);
        assertEquals(2, structure.effect.parameters.get("amount"));
        
        // Test text generation
        assertEquals("Deal 2 damage to any number of target creatures and/or players", 
            AbilityGenerator.generateAbility(structure));
    }
} 