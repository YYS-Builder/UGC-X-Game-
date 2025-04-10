package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class AbilityValidatorTest {
    private AbilityValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new AbilityValidator();
    }
    
    @Test
    void testValidateNumericalEffect() {
        // Valid chain with numerical effect and value
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "3"));
        assertTrue(validator.validate(validChain));
        
        // Invalid chain with numerical effect but no value
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        assertFalse(validator.validate(invalidChain));
        
        // Valid chain with non-numerical effect and no value
        TargetingChain nonNumericalChain = new TargetingChain();
        nonNumericalChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "destroy"));
        assertTrue(validator.validate(nonNumericalChain));
    }
    
    @Test
    void testValidateTargetType() {
        // Valid chain with target direction and type
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "target"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "creature"));
        assertTrue(validator.validate(validChain));
        
        // Invalid chain with target direction but no type
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "target"));
        assertFalse(validator.validate(invalidChain));
        
        // Valid chain with non-target direction and no type
        TargetingChain nonTargetChain = new TargetingChain();
        nonTargetChain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "all"));
        assertTrue(validator.validate(nonTargetChain));
    }
    
    @Test
    void testValidateTriggerEffect() {
        // Valid chain with trigger and effect
        TargetingChain validChain = new TargetingChain();
        validChain.addComponent(new TargetingComponent(ComponentCategory.TRIGGER, "when"));
        validChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "draw"));
        assertTrue(validator.validate(validChain));
        
        // Invalid chain with trigger but no effect
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.TRIGGER, "whenever"));
        assertFalse(validator.validate(invalidChain));
    }
    
    @Test
    void testCustomValidators() {
        // Test damage validator
        TargetingChain damageChain = new TargetingChain();
        damageChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        damageChain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "3"));
        damageChain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "target"));
        damageChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "creature"));
        assertTrue(validator.validate(damageChain));
        
        // Test invalid damage value
        TargetingChain invalidDamageChain = new TargetingChain();
        invalidDamageChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        invalidDamageChain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "0"));
        invalidDamageChain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "target"));
        invalidDamageChain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "creature"));
        assertFalse(validator.validate(invalidDamageChain));
    }
    
    @Test
    void testGetValidationErrors() {
        // Chain with multiple validation errors
        TargetingChain invalidChain = new TargetingChain();
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        invalidChain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "target"));
        
        List<String> errors = validator.getValidationErrors(invalidChain);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("value")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("type")));
    }
    
    @Test
    void testAddCustomRule() {
        ValidationRule customRule = new ValidationRule(
            "Custom Rule",
            "Custom validation rule",
            chain -> chain.getComponents().size() > 0,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
        
        validator.addRule(customRule);
        
        // Test empty chain
        TargetingChain emptyChain = new TargetingChain();
        assertFalse(validator.validate(emptyChain));
        
        List<String> errors = validator.getValidationErrors(emptyChain);
        assertTrue(errors.contains("Custom validation rule"));
    }
    
    @Test
    void testAddCustomValidator() {
        validator.addCustomValidator("custom", chain -> 
            chain.getComponents().size() >= 2
        );
        
        // Create a chain that matches the custom pattern
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "custom"));
        
        // Should fail because it has only one component
        assertFalse(validator.validate(chain));
        
        // Add another component
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "1"));
        assertTrue(validator.validate(chain));
    }
} 