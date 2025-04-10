package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SnapTrainingTest {
    private SnapAbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new SnapAbilityParser();
    }
    
    @Test
    void testOngoingAbility() {
        // Example: Ant-Man
        String ability = "Ongoing: +3 Power if you have 3 other cards here";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "ongoing"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "3"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "power"));
    }
    
    @Test
    void testOnRevealAbility() {
        // Example: Ironheart
        String ability = "On Reveal: Give +2 Power to 3 other cards at random";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "on reveal"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "add"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "power"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "random"));
    }
    
    @Test
    void testLocationAbility() {
        // Example: Sanctum Sanctorum
        String ability = "You can't play cards here";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "location"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "play"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "can't"));
    }
    
    @Test
    void testCardMovement() {
        // Example: Nightcrawler
        String ability = "You can move this once";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "move"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "this"));
    }
    
    @Test
    void testPowerModification() {
        // Example: Blue Marvel
        String ability = "Ongoing: Your other cards have +1 Power";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "ongoing"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "add"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "power"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
    }
    
    @Test
    void testCardDestruction() {
        // Example: Killmonger
        String ability = "On Reveal: Destroy all 1-Cost cards";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "on reveal"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "destroy"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.ENERGY, "1"));
    }
    
    @Test
    void testCardCopying() {
        // Example: Mystique
        String ability = "On Reveal: Copy the text from your last played Ongoing card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "on reveal"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "copy"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "text"));
    }
    
    @Test
    void testCardTransformation() {
        // Example: Scarlet Witch
        String ability = "On Reveal: Replace this location with a random new one";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "on reveal"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "transform"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "this"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "location"));
    }
    
    @Test
    void testCardSwapping() {
        // Example: Cloak
        String ability = "On Reveal: Move your other cards here to adjacent locations";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "on reveal"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "move"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.POSITION, "adjacent"));
    }
    
    @Test
    void testMultipleEffects() {
        // Example: Doctor Doom
        String ability = "On Reveal: Add a 5-Power DoomBot to each other location";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "on reveal"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "add"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "5"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "power"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "each"));
    }
    
    @Test
    void testChainValidation() {
        // Test a valid ongoing chain
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "ongoing"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "add"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "power"));
        
        assertTrue(parser.isValidChain(validChain));
        
        // Test an invalid chain (missing required components)
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "ongoing"));
        
        assertFalse(parser.isValidChain(invalidChain));
    }
    
    @Test
    void testTextGeneration() {
        // Test generating text from a chain
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "on reveal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "add"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "power"));
        
        String generatedText = parser.generateText(chain);
        assertEquals("on reveal add 2 power", generatedText);
    }
} 