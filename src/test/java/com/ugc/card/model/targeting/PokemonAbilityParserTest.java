package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PokemonAbilityParserTest {
    private PokemonAbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new PokemonAbilityParser();
    }
    
    @Test
    void testParsePokePower() {
        String ability = "Poke-Power: Draw 2 cards";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testParsePokeBody() {
        String ability = "Poke-Body: Prevent all damage done to this Pokemon by your opponent's Pokemon V";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "prevent"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "damage"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
    }
    
    @Test
    void testParseEnergyAttachment() {
        String ability = "Attach a Grass Energy from your hand to 1 of your Pokemon";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "attach"));
        assertTrue(chain.hasComponent(ComponentCategory.ENERGY_TYPE, "grass"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "pokemon"));
    }
    
    @Test
    void testParseEvolution() {
        String ability = "Evolve your Pokemon";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "evolve"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "pokemon"));
    }
    
    @Test
    void testIsValidChain() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "attach"));
        chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "your"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "pokemon"));
        
        assertTrue(parser.isValidChain(chain));
    }
    
    @Test
    void testGenerateText() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "draw"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "card"));
        
        String text = parser.generateText(chain);
        assertEquals("draw 2 card", text);
    }
    
    @Test
    void testGetValidCategories() {
        Set<ComponentCategory> categories = parser.getValidCategories();
        assertTrue(categories.contains(ComponentCategory.TYPE));
        assertTrue(categories.contains(ComponentCategory.EFFECT));
        assertTrue(categories.contains(ComponentCategory.DIRECTION));
        assertTrue(categories.contains(ComponentCategory.ENERGY_TYPE));
    }
    
    @Test
    void testGetValidComponents() {
        Set<String> effects = parser.getValidComponents(ComponentCategory.EFFECT);
        assertTrue(effects.contains("attach"));
        assertTrue(effects.contains("draw"));
        assertTrue(effects.contains("evolve"));
        
        Set<String> energyTypes = parser.getValidComponents(ComponentCategory.ENERGY_TYPE);
        assertTrue(energyTypes.contains("grass"));
        assertTrue(energyTypes.contains("fire"));
        assertTrue(energyTypes.contains("water"));
    }
    
    @Test
    void testGetValidationRules() {
        List<ValidationRule> rules = parser.getValidationRules();
        assertFalse(rules.isEmpty());
        assertTrue(rules.stream().anyMatch(rule -> rule.getName().equals("Energy Attachment Rule")));
        assertTrue(rules.stream().anyMatch(rule -> rule.getName().equals("Evolution Rule")));
    }
    
    @Test
    void testGetAbilityTypes() {
        List<AbilityType> types = parser.getAbilityTypes();
        assertFalse(types.isEmpty());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Poke-Power")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Poke-Body")));
    }
    
    @Test
    void testGetTargetingPatterns() {
        List<TargetingPattern> patterns = parser.getTargetingPatterns();
        assertFalse(patterns.isEmpty());
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Poke-Power")));
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Poke-Body")));
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("regular")));
    }
} 