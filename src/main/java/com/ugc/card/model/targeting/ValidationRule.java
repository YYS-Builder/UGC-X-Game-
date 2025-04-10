package com.ugc.card.model.targeting;

import java.util.*;
import java.util.function.Predicate;

public class ValidationRule {
    private String name;
    private String description;
    private Predicate<TargetingChain> validator;
    private Set<ComponentCategory> requiredCategories;
    private Set<ComponentCategory> forbiddenCategories;
    private Set<String> requiredComponents;
    private Set<String> forbiddenComponents;
    
    public ValidationRule(String name,
                         String description,
                         Predicate<TargetingChain> validator,
                         Set<ComponentCategory> requiredCategories,
                         Set<ComponentCategory> forbiddenCategories,
                         Set<String> requiredComponents,
                         Set<String> forbiddenComponents) {
        this.name = name;
        this.description = description;
        this.validator = validator;
        this.requiredCategories = requiredCategories;
        this.forbiddenCategories = forbiddenCategories;
        this.requiredComponents = requiredComponents;
        this.forbiddenComponents = forbiddenComponents;
    }
    
    public boolean validate(TargetingChain chain) {
        // Check required categories
        for (ComponentCategory category : requiredCategories) {
            if (!chain.hasComponent(category)) {
                return false;
            }
        }
        
        // Check forbidden categories
        for (ComponentCategory category : forbiddenCategories) {
            if (chain.hasComponent(category)) {
                return false;
            }
        }
        
        // Check required components
        for (String component : requiredComponents) {
            boolean found = false;
            for (TargetingComponent chainComponent : chain.getComponents()) {
                if (chainComponent.getText().equals(component)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        
        // Check forbidden components
        for (String component : forbiddenComponents) {
            for (TargetingComponent chainComponent : chain.getComponents()) {
                if (chainComponent.getText().equals(component)) {
                    return false;
                }
            }
        }
        
        // Apply custom validation
        return validator.test(chain);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Set<ComponentCategory> getRequiredCategories() {
        return requiredCategories;
    }
    
    public Set<ComponentCategory> getForbiddenCategories() {
        return forbiddenCategories;
    }
    
    public Set<String> getRequiredComponents() {
        return requiredComponents;
    }
    
    public Set<String> getForbiddenComponents() {
        return forbiddenComponents;
    }
    
    @Override
    public String toString() {
        return "ValidationRule{" +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", requiredCategories=" + requiredCategories +
               ", forbiddenCategories=" + forbiddenCategories +
               ", requiredComponents=" + requiredComponents +
               ", forbiddenComponents=" + forbiddenComponents +
               '}';
    }
} 