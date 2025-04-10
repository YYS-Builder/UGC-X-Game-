package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StarWarsTrainingTest {
    private StarWarsAbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new StarWarsAbilityParser();
    }
    
    @Test
    void testCharacterAbility() {
        // Example: Luke Skywalker
        String ability = "When this character attacks, you may deal 1 damage to target character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "attacking"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "deal"));
        assertTrue(chain.hasComponent(ComponentCategory.DAMAGE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
    }
    
    @Test
    void testVehicleAbility() {
        // Example: Millennium Falcon
        String ability = "When this vehicle moves, you may ready another vehicle";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "vehicle"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "moved"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
    }
    
    @Test
    void testWeaponAbility() {
        // Example: Lightsaber
        String ability = "When this weapon is equipped, the character gains +2 attack";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "weapon"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "equipped"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "attack"));
    }
    
    @Test
    void testForceAbility() {
        // Example: Force Push
        String ability = "When you play this event, you may move target character to another battlefield";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "event"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "move"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "battlefield"));
    }
    
    @Test
    void testDamageAbility() {
        // Example: Thermal Detonator
        String ability = "When this event is played, deal 2 damage to each character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "event"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "deal"));
        assertTrue(chain.hasComponent(ComponentCategory.DAMAGE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "each"));
    }
    
    @Test
    void testShieldAbility() {
        // Example: Deflector Shield
        String ability = "When this upgrade is played, target vehicle gains shield 2";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "upgrade"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "vehicle"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
    }
    
    @Test
    void testExhaustAbility() {
        // Example: Force Choke
        String ability = "When this event is played, exhaust target character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "event"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "exhaust"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
    }
    
    @Test
    void testMultipleEffects() {
        // Example: Darth Vader
        String ability = "When this character attacks, you may exhaust another character and deal 1 damage to it";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "attacking"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "exhaust"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "deal"));
        assertTrue(chain.hasComponent(ComponentCategory.DAMAGE, "1"));
    }
    
    @Test
    void testChainValidation() {
        // Test a valid character ability chain
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "character"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "attack"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "character"));
        
        assertTrue(parser.isValidChain(validChain));
        
        // Test an invalid chain (missing required components)
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "character"));
        
        assertFalse(parser.isValidChain(invalidChain));
    }
    
    @Test
    void testTextGeneration() {
        // Test generating text from a chain
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "character"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "attack"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.DAMAGE, "1"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "character"));
        
        String generatedText = parser.generateText(chain);
        assertEquals("character attack deal 1 character", generatedText);
    }
    
    @Test
    void testForceAlignmentAbility() {
        // Example: Yoda
        String ability = "When this character is played, you may look at the top 3 cards of your deck and add 1 light side card to your hand";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "look"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "3"));
        assertTrue(chain.hasComponent(ComponentCategory.FORCE, "light"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "add"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "hand"));
    }
    
    @Test
    void testMissionAbility() {
        // Example: Rescue the Princess
        String ability = "When this mission is completed, you may ready all your characters";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "mission"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "completed"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
    }
    
    @Test
    void testObjectiveAbility() {
        // Example: Destroy the Death Star
        String ability = "When this objective is completed, deal 5 damage to target vehicle";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "objective"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "completed"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "deal"));
        assertTrue(chain.hasComponent(ComponentCategory.DAMAGE, "5"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "vehicle"));
    }
    
    @Test
    void testBattlefieldAbility() {
        // Example: Death Star
        String ability = "When this battlefield is played, all characters gain +1 attack";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "battlefield"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "attack"));
    }
    
    @Test
    void testSupportAbility() {
        // Example: Rebel Base
        String ability = "When this support is played, you may search your deck for a character and add it to your hand";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "support"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "search"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "deck"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "add"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "hand"));
    }
    
    @Test
    void testComplexVehicleAbility() {
        // Example: Millennium Falcon
        String ability = "When this vehicle moves, you may ready another vehicle and draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "vehicle"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "moved"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testInvalidAbility() {
        // Test an invalid ability format
        String ability = "This is not a valid ability";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testEmptyAbility() {
        // Test an empty ability
        String ability = "";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testNullAbility() {
        // Test a null ability
        assertThrows(NullPointerException.class, () -> {
            parser.parseAbility(null);
        });
    }
    
    @Test
    void testComponentRetrieval() {
        // Test retrieving valid components for each category
        Set<ComponentCategory> categories = parser.getValidCategories();
        assertTrue(categories.contains(ComponentCategory.TYPE));
        assertTrue(categories.contains(ComponentCategory.EFFECT));
        assertTrue(categories.contains(ComponentCategory.DIRECTION));
        assertTrue(categories.contains(ComponentCategory.VALUE));
        assertTrue(categories.contains(ComponentCategory.STATE));
        assertTrue(categories.contains(ComponentCategory.TARGET));
        assertTrue(categories.contains(ComponentCategory.FORCE));
        assertTrue(categories.contains(ComponentCategory.DAMAGE));
    }
    
    @Test
    void testValidationRules() {
        // Test retrieving validation rules
        List<ValidationRule> rules = parser.getValidationRules();
        assertFalse(rules.isEmpty());
        assertTrue(rules.stream().anyMatch(rule -> rule.getName().equals("Character Ability Rule")));
        assertTrue(rules.stream().anyMatch(rule -> rule.getName().equals("Vehicle Ability Rule")));
    }
    
    @Test
    void testAbilityTypes() {
        // Test retrieving ability types
        List<AbilityType> types = parser.getAbilityTypes();
        assertFalse(types.isEmpty());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Character Ability")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Vehicle Ability")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Weapon Ability")));
    }
    
    @Test
    void testTargetingPatterns() {
        // Test retrieving targeting patterns
        List<TargetingPattern> patterns = parser.getTargetingPatterns();
        assertFalse(patterns.isEmpty());
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Character Ability")));
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Vehicle Ability")));
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Weapon Ability")));
    }
    
    @Test
    void testComplexForceAlignment() {
        // Example: Yoda with complex force alignment
        String ability = "When this character is played, you may look at the top 3 cards of your deck. If you find a light side card, you may add it to your hand. If you find a dark side card, you may banish it";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "look"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "3"));
        assertTrue(chain.hasComponent(ComponentCategory.FORCE, "light"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "add"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "hand"));
        assertTrue(chain.hasComponent(ComponentCategory.FORCE, "dark"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "banish"));
    }
    
    @Test
    void testComplexMissionInteraction() {
        // Example: Rescue the Princess with complex mission interaction
        String ability = "When this mission is completed, if you control a Rebel character, you may ready all your characters and draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "mission"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "completed"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "control"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testComplexVehicleMovement() {
        // Example: Millennium Falcon with complex movement
        String ability = "When this vehicle moves, you may ready another vehicle. If you do, you may move that vehicle to another battlefield";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "vehicle"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "moved"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "move"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "battlefield"));
    }
    
    @Test
    void testComplexWeaponInteraction() {
        // Example: Lightsaber with complex weapon interaction
        String ability = "When this weapon is equipped to a Jedi character, it gains +2 attack. If it's equipped to a Sith character, it gains +3 attack instead";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "weapon"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "equipped"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "attack"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "3"));
    }
    
    @Test
    void testComplexBattlefieldEffect() {
        // Example: Death Star with complex battlefield effect
        String ability = "When this battlefield is played, all characters gain +1 attack. If a character would be destroyed, you may exhaust it instead";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "battlefield"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "attack"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "exhaust"));
    }
    
    @Test
    void testInvalidForceAlignment() {
        // Test an ability with invalid force alignment
        String ability = "When this character is played, you may look at the top 3 cards of your deck and add a neutral side card to your hand";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testInvalidMissionState() {
        // Test an ability with invalid mission state
        String ability = "When this mission is played, you may ready all your characters";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testInvalidVehicleMovement() {
        // Test an ability with invalid vehicle movement
        String ability = "When this vehicle moves, you may move it to the same battlefield";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testInvalidWeaponTarget() {
        // Test an ability with invalid weapon target
        String ability = "When this weapon is equipped to a battlefield, it gains +2 attack";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testInvalidBattlefieldEffect() {
        // Test an ability with invalid battlefield effect
        String ability = "When this battlefield is played, you may move it to another battlefield";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
} 