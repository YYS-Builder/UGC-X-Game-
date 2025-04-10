package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FleshAndBloodTrainingTest {
    private FleshAndBloodAbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new FleshAndBloodAbilityParser();
    }
    
    @Test
    void testAttackAction() {
        // Example: Red Liner
        String ability = "Attack 4";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "attack"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "4"));
    }
    
    @Test
    void testDefenseReaction() {
        // Example: Sink Below
        String ability = "Defense Reaction: Block 4";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "defense"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "block"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "4"));
    }
    
    @Test
    void testEquipmentAbility() {
        // Example: Fyendal's Spring Tunic
        String ability = "Once per turn, you may gain 1 resource.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "resource"));
    }
    
    @Test
    void testPitchValue() {
        // Example: Red, Yellow, Blue cards
        String ability = "Pitch 3 for red";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "pitch"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "3"));
        assertTrue(chain.hasComponent(ComponentCategory.PITCH_VALUE, "red"));
    }
    
    @Test
    void testChainLink() {
        // Example: Chain abilities
        String ability = "If this is the first chain link, draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.CHAIN_LINK, "first"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testAttackReaction() {
        // Example: Razor Reflex
        String ability = "Attack Reaction: If the defending hero blocks, they discard a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "attack"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "blocked"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "discard"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testWeaponAbility() {
        // Example: Dawnblade
        String ability = "Once per turn, if you've played an attack action card this turn, this weapon gains +1 attack";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "weapon"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "attack"));
    }
    
    @Test
    void testResourceGeneration() {
        // Example: Enlightened Strike
        String ability = "If you control an Illusionist card, this attack gains +2 attack";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "attack"));
    }
    
    @Test
    void testStateModification() {
        // Example: Exhausted state
        String ability = "Target attack action card becomes exhausted";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "attack"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "exhausted"));
    }
    
    @Test
    void testMultipleEffects() {
        // Example: Command and Conquer
        String ability = "Attack 6. If this hits, destroy target equipment";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "attack"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "6"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "destroy"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "equipment"));
    }
    
    @Test
    void testChainValidation() {
        // Test a valid attack chain
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "attack"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "4"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "hero"));
        
        assertTrue(parser.isValidChain(validChain));
        
        // Test an invalid chain (missing required components)
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "attack"));
        
        assertFalse(parser.isValidChain(invalidChain));
    }
    
    @Test
    void testTextGeneration() {
        // Test generating text from a chain
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "attack"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "6"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "hero"));
        
        String generatedText = parser.generateText(chain);
        assertEquals("attack 6 hero", generatedText);
    }
} 