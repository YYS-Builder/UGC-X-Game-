package com.ugc.card.model.targeting;

import java.util.*;

public class AbilityType {
    private final String name;
    private final String description;
    private final Set<ComponentCategory> requiredCategories;
    private final Set<ComponentCategory> optionalCategories;
    private final Map<ComponentCategory, Set<String>> validValues;
    private final Set<ValidationRule> validationRules;
    private final String template;
    
    public AbilityType(String name, String description,
                      Set<ComponentCategory> requiredCategories,
                      Set<ComponentCategory> optionalCategories,
                      Map<ComponentCategory, Set<String>> validValues,
                      Set<ValidationRule> validationRules,
                      String template) {
        this.name = name;
        this.description = description;
        this.requiredCategories = requiredCategories;
        this.optionalCategories = optionalCategories;
        this.validValues = validValues;
        this.validationRules = validationRules;
        this.template = template;
    }
    
    public boolean isValid(TargetingChain chain) {
        // Check required categories
        for (ComponentCategory category : requiredCategories) {
            if (!chain.hasComponent(category)) {
                return false;
            }
        }
        
        // Check component validity
        for (TargetingComponent component : chain.getComponents()) {
            ComponentCategory category = component.getCategory();
            if (validValues.containsKey(category)) {
                if (!validValues.get(category).contains(component.getText())) {
                    return false;
                }
            }
        }
        
        // Check specific rules
        for (ValidationRule rule : validationRules) {
            if (!rule.validate(chain)) {
                return false;
            }
        }
        
        return true;
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
    
    public Set<ComponentCategory> getOptionalCategories() {
        return optionalCategories;
    }
    
    public Map<ComponentCategory, Set<String>> getValidValues() {
        return validValues;
    }
    
    public Set<ValidationRule> getValidationRules() {
        return validationRules;
    }
    
    public String getTemplate() {
        return template;
    }
} 