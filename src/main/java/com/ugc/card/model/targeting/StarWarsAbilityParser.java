package com.ugc.card.model.targeting;

import java.util.*;

public class StarWarsAbilityParser implements AbilityParser {
    private final Map<ComponentCategory, Set<String>> validValues;
    private final Set<ComponentCategory> validCategories;
    
    public StarWarsAbilityParser() {
        this.validValues = new HashMap<>();
        this.validCategories = new HashSet<>();
        
        validCategories.add(ComponentCategory.TARGET);
        validCategories.add(ComponentCategory.ACTION);
        validCategories.add(ComponentCategory.CONDITION);
        validCategories.add(ComponentCategory.MODIFIER);
        
        validValues.put(ComponentCategory.TARGET, Set.of("character", "vehicle", "weapon"));
        validValues.put(ComponentCategory.ACTION, Set.of("attack", "defend", "move", "use"));
        validValues.put(ComponentCategory.CONDITION, Set.of("if", "when", "after"));
        validValues.put(ComponentCategory.MODIFIER, Set.of("all", "each", "random"));
    }
    
    @Override
    public Set<String> getValidComponents(ComponentCategory category) {
        return validValues.getOrDefault(category, Collections.emptySet());
    }
    
    @Override
    public Set<ComponentCategory> getValidCategories() {
        return validCategories;
    }
    
    @Override
    public List<TargetingComponent> getComponents() {
        List<TargetingComponent> components = new ArrayList<>();
        for (Map.Entry<ComponentCategory, Set<String>> entry : validValues.entrySet()) {
            for (String value : entry.getValue()) {
                components.add(new TargetingComponent(entry.getKey(), value));
            }
        }
        return components;
    }
    
    @Override
    public TargetingChain parseAbility(String abilityText) {
        // Implementation of parseAbility
        return null;
    }
    
    @Override
    public Set<ValidationRule> getValidationRules() {
        return Collections.emptySet();
    }
    
    @Override
    public Set<AbilityType> getAbilityTypes() {
        return Collections.emptySet();
    }
    
    @Override
    public Set<TargetingPattern> getTargetingPatterns() {
        return Collections.emptySet();
    }
    
    @Override
    public Map<ComponentCategory, Set<String>> getValidComponents() {
        return validValues;
    }
    
    @Override
    public boolean isValidChain(TargetingChain chain) {
        return true;
    }
    
    @Override
    public String generateText(TargetingChain chain) {
        return "";
    }

    public Set<TargetingComponent> convertToComponents(Set<String> values, ComponentCategory category) {
        Set<TargetingComponent> components = new HashSet<>();
        for (String value : values) {
            components.add(new TargetingComponent(category, value));
        }
        return components;
    }

    public List<TargetingComponent> convertToComponentList(Set<String> values, ComponentCategory category) {
        List<TargetingComponent> components = new ArrayList<>();
        for (String value : values) {
            components.add(new TargetingComponent(category, value));
        }
        return components;
    }

    public Set<TargetingComponent> getComponentsByCategory(ComponentCategory category) {
        Set<String> values = getValidComponents(category);
        return convertToComponents(values, category);
    }

    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        Set<String> values = getValidComponents(category);
        return convertToComponentList(values, category);
    }
} 