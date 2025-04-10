package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YugiohTrainingTest {
    private YugiohAbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new YugiohAbilityParser();
    }
    
    @Test
    void testMonsterEffect() {
        // Example: Dark Magician
        String ability = "You can Special Summon this card from your hand by Tributing 1 face-up monster you control";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "special summon"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "hand"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "tribute"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "face-up"));
    }
    
    @Test
    void testSpellEffect() {
        // Example: Dark Hole
        String ability = "Destroy all monsters on the field";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "destroy"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "monster"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "field"));
    }
    
    @Test
    void testTrapEffect() {
        // Example: Mirror Force
        String ability = "When an opponent's monster declares an attack: Destroy all Attack Position monsters your opponent controls";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "opponent"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "attacking"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "destroy"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "attack position"));
    }
    
    @Test
    void testAttributeEffect() {
        // Example: Elemental HERO Stratos
        String ability = "When this card is Normal or Special Summoned: You can destroy Spell/Trap cards on the field, up to the number of face-up "WIND" monsters you control";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "summoned"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "destroy"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "spell/trap zone"));
        assertTrue(chain.hasComponent(ComponentCategory.ATTRIBUTE, "wind"));
    }
    
    @Test
    void testLevelEffect() {
        // Example: Stardust Dragon
        String ability = "When a card or effect is activated that would destroy a card(s) on the field: You can Tribute this card; negate the activation, and if you do, destroy it";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "tribute"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "negate"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "destroy"));
    }
    
    @Test
    void testPendulumEffect() {
        // Example: Odd-Eyes Pendulum Dragon
        String ability = "If this card battles an opponent's monster, any battle damage this card inflicts to your opponent is doubled";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "pendulum"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "opponent"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "double"));
    }
    
    @Test
    void testLinkEffect() {
        // Example: Linkuriboh
        String ability = "When an opponent's monster declares an attack: You can Tribute this card; change that opponent's monster's ATK to 0, until the end of this turn";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "link"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "tribute"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "change"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "0"));
    }
    
    @Test
    void testXYZEffect() {
        // Example: Number 39: Utopia
        String ability = "When any player's monster declares an attack: You can detach 1 Xyz Material from this card; negate the attack";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "xyz"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "detach"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "negate"));
    }
    
    @Test
    void testSynchroEffect() {
        // Example: Stardust Dragon
        String ability = "When a card or effect is activated that would destroy a card(s) on the field: You can Tribute this card; negate the activation, and if you do, destroy it";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "synchro"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "tribute"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "negate"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "destroy"));
    }
    
    @Test
    void testMultipleEffects() {
        // Example: Dark Magician Girl
        String ability = "This card gains 300 ATK for every \"Dark Magician\" or \"Magician of Black Chaos\" in either player's Graveyard";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "300"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "graveyard"));
    }
    
    @Test
    void testChainValidation() {
        // Test a valid monster effect chain
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "monster"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "destroy"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "monster"));
        
        assertTrue(parser.isValidChain(validChain));
        
        // Test an invalid chain (missing required components)
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "monster"));
        
        assertFalse(parser.isValidChain(invalidChain));
    }
    
    @Test
    void testTextGeneration() {
        // Test generating text from a chain
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "monster"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "destroy"));
        chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "all"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "monster"));
        
        String generatedText = parser.generateText(chain);
        assertEquals("monster destroy all monster", generatedText);
    }
} 