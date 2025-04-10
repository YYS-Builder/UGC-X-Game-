package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PokemonTrainingTest {
    private PokemonAbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new PokemonAbilityParser();
    }
    
    @Test
    void testBasicPokePower() {
        // Example: Pikachu's Thunder Wave
        String ability = "Poke-Power: Flip a coin. If heads, the Defending Pokemon is now Paralyzed.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "flip"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "coin"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "paralyzed"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "defending"));
    }
    
    @Test
    void testEnergyBasedAbility() {
        // Example: Charizard's Fire Spin
        String ability = "Discard 2 Energy cards attached to Charizard in order to use this attack.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "discard"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "energy"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "attached"));
    }
    
    @Test
    void testEvolutionAbility() {
        // Example: Eevee's Evolution
        String ability = "Search your deck for a card that evolves from Eevee and put it on Eevee.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "search"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "deck"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "evolve"));
    }
    
    @Test
    void testPokeBody() {
        // Example: Blastoise's Rain Dance
        String ability = "Poke-Body: As often as you like during your turn, you may attach a Water Energy card to 1 of your Water Pokemon.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "attach"));
        assertTrue(chain.hasComponent(ComponentCategory.ENERGY_TYPE, "water"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "pokemon"));
    }
    
    @Test
    void testDamageModification() {
        // Example: Mewtwo's Barrier
        String ability = "During your opponent's next turn, prevent all effects of attacks, including damage, done to Mewtwo.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "prevent"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "damage"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "next turn"));
    }
    
    @Test
    void testStatusEffect() {
        // Example: Venusaur's Poison Powder
        String ability = "The Defending Pokemon is now Poisoned.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "poisoned"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "defending"));
    }
    
    @Test
    void testCardSearch() {
        // Example: Computer Search
        String ability = "Discard 2 cards from your hand in order to search your deck for any card and put it into your hand.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "discard"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "search"));
    }
    
    @Test
    void testEnergyAcceleration() {
        // Example: Energy Search
        String ability = "Search your deck for a basic Energy card, show it to your opponent, and put it into your hand.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "search"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "deck"));
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "energy"));
    }
    
    @Test
    void testRetreatCost() {
        // Example: Snorlax's Heavy Sleep
        String ability = "Snorlax is now Asleep. Snorlax can't retreat while Asleep.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "asleep"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "retreat"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "can't"));
    }
    
    @Test
    void testMultipleEffects() {
        // Example: Professor Oak
        String ability = "Discard your hand, then draw 7 cards.";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "discard"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "7"));
    }
    
    @Test
    void testChainValidation() {
        // Test a valid chain
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "attach"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.ENERGY_TYPE, "water"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "your"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "pokemon"));
        
        assertTrue(parser.isValidChain(validChain));
        
        // Test an invalid chain (missing required components)
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "attach"));
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.ENERGY_TYPE, "water"));
        
        assertFalse(parser.isValidChain(invalidChain));
    }
    
    @Test
    void testTextGeneration() {
        // Test generating text from a chain
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "draw"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "3"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "card"));
        chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "your"));
        
        String generatedText = parser.generateText(chain);
        assertEquals("draw 3 card your", generatedText);
    }
} 