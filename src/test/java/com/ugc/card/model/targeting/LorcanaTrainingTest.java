package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LorcanaTrainingTest {
    private LorcanaAbilityParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new LorcanaAbilityParser();
    }
    
    @Test
    void testCharacterAbility() {
        // Example: Mickey Mouse - Brave Little Tailor
        String ability = "When this character quests, you may draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "quest"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testSongAbility() {
        // Example: Let It Go
        String ability = "When you sing this song, you may banish a chosen character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "song"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "sung"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "banish"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
    }
    
    @Test
    void testItemAbility() {
        // Example: Magic Broom - Bucket Brigade
        String ability = "When you play this item, you may ready one of your exerted characters";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "item"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "exerted"));
    }
    
    @Test
    void testInkAbility() {
        // Example: Maleficent - Monstrous Dragon
        String ability = "When you play this character, you may pay 2 ink to challenge an opponent's character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "challenge"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "opponent"));
    }
    
    @Test
    void testLoreAbility() {
        // Example: Elsa - Spirit of Winter
        String ability = "When this character quests, gain 2 lore";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "quest"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.LORE, "2"));
    }
    
    @Test
    void testChallengeAbility() {
        // Example: Aladdin - Heroic Outlaw
        String ability = "When this character challenges, it gains +2 strength";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "challenge"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
    }
    
    @Test
    void testExertAbility() {
        // Example: Captain Hook - Forceful Duelist
        String ability = "When this character is exerted, you may ready another character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "exerted"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
    }
    
    @Test
    void testMultipleEffects() {
        // Example: Ursula - Power Hungry
        String ability = "When you play this character, you may exert another character and draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "exert"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testChainValidation() {
        // Test a valid character ability chain
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "character"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "quest"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "card"));
        
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
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "quest"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "draw"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "card"));
        
        String generatedText = parser.generateText(chain);
        assertEquals("character quest draw card", generatedText);
    }
    
    @Test
    void testInkCostAbility() {
        // Example: Maleficent - Monstrous Dragon
        String ability = "When you play this character, you may pay 2 ink to challenge an opponent's character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "challenge"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "opponent"));
    }
    
    @Test
    void testMultiInkAbility() {
        // Example: Mickey Mouse - Detective
        String ability = "When you play this character, you may pay 1 ink of each color to draw 2 cards";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testLocationAbility() {
        // Example: Beast's Castle
        String ability = "When you play this location, you may ready all your exerted characters";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "location"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "exerted"));
    }
    
    @Test
    void testActionAbility() {
        // Example: Let It Go
        String ability = "When you play this action, you may banish target character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "action"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "banish"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
    }
    
    @Test
    void testQuestingAbility() {
        // Example: Ariel - On Human Legs
        String ability = "When this character quests, you may draw a card and gain 1 lore";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "quest"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.LORE, "1"));
    }
    
    @Test
    void testChallengingAbility() {
        // Example: Aladdin - Heroic Outlaw
        String ability = "When this character challenges, it gains +2 strength and deals 1 damage to the challenged character";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "challenge"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "deal"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
    }
    
    @Test
    void testComplexAbility() {
        // Example: Ursula - Power Hungry
        String ability = "When you play this character, you may exert another character, draw a card, and gain 1 lore";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "exert"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.LORE, "1"));
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
        assertTrue(categories.contains(ComponentCategory.INK));
        assertTrue(categories.contains(ComponentCategory.LORE));
    }
    
    @Test
    void testValidationRules() {
        // Test retrieving validation rules
        List<ValidationRule> rules = parser.getValidationRules();
        assertFalse(rules.isEmpty());
        assertTrue(rules.stream().anyMatch(rule -> rule.getName().equals("Character Ability Rule")));
        assertTrue(rules.stream().anyMatch(rule -> rule.getName().equals("Song Ability Rule")));
    }
    
    @Test
    void testAbilityTypes() {
        // Test retrieving ability types
        List<AbilityType> types = parser.getAbilityTypes();
        assertFalse(types.isEmpty());
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Character Ability")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Song Ability")));
        assertTrue(types.stream().anyMatch(type -> type.getName().equals("Item Ability")));
    }
    
    @Test
    void testTargetingPatterns() {
        // Test retrieving targeting patterns
        List<TargetingPattern> patterns = parser.getTargetingPatterns();
        assertFalse(patterns.isEmpty());
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Character Ability")));
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Song Ability")));
        assertTrue(patterns.stream().anyMatch(pattern -> pattern.getName().equals("Item Ability")));
    }
    
    @Test
    void testComplexInkInteraction() {
        // Example: Maleficent - Monstrous Dragon with complex ink interaction
        String ability = "When you play this character, you may pay 2 ink of any color to challenge an opponent's character. If you do, you may pay 1 additional ink to draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "challenge"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "opponent"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
    }
    
    @Test
    void testConditionalQuesting() {
        // Example: Ariel - On Human Legs with conditional questing
        String ability = "When this character quests, if you control another character, you may draw a card and gain 1 lore";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "quest"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "control"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.LORE, "1"));
    }
    
    @Test
    void testMultiTargetChallenging() {
        // Example: Aladdin - Heroic Outlaw with multi-target challenging
        String ability = "When this character challenges, it gains +2 strength and deals 1 damage to each character the opponent controls";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "challenge"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "2"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "deal"));
        assertTrue(chain.hasComponent(ComponentCategory.VALUE, "1"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "each"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "opponent"));
    }
    
    @Test
    void testComplexSongInteraction() {
        // Example: Let It Go with complex song interaction
        String ability = "When you sing this song, you may banish a chosen character. If you do, you may ready all your exerted characters";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "song"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "sung"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "banish"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "ready"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "your"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "exerted"));
    }
    
    @Test
    void testNestedConditionalEffects() {
        // Example: Ursula - Power Hungry with nested conditionals
        String ability = "When you play this character, if you control another character, you may exert it. If you do, draw a card and gain 1 lore";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertTrue(chain.hasComponent(ComponentCategory.TYPE, "character"));
        assertTrue(chain.hasComponent(ComponentCategory.STATE, "played"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "control"));
        assertTrue(chain.hasComponent(ComponentCategory.DIRECTION, "another"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "exert"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(chain.hasComponent(ComponentCategory.TARGET, "card"));
        assertTrue(chain.hasComponent(ComponentCategory.EFFECT, "gain"));
        assertTrue(chain.hasComponent(ComponentCategory.LORE, "1"));
    }
    
    @Test
    void testInvalidComponentCombination() {
        // Test an ability with invalid component combinations
        String ability = "When this character quests, you may exert a song";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testMissingRequiredComponents() {
        // Test an ability missing required components
        String ability = "When this character quests";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testDuplicateComponents() {
        // Test an ability with duplicate components
        String ability = "When this character quests, you may draw a card and draw a card";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testInvalidValueRange() {
        // Test an ability with invalid value range
        String ability = "When this character quests, gain 11 lore";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
    
    @Test
    void testInvalidStateTransition() {
        // Test an ability with invalid state transition
        String ability = "When this character is exerted, you may exert it";
        TargetingChain chain = parser.parseAbility(ability);
        
        assertFalse(parser.isValidChain(chain));
    }
} 