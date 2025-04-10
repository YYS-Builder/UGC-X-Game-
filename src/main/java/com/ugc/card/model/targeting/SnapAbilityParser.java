package com.ugc.card.model.targeting;

import java.util.*;

public class SnapAbilityParser implements AbilityParser {
    private final Map<ComponentCategory, Set<String>> validValues;
    private final Set<ComponentCategory> validCategories;
    private final Set<ValidationRule> validationRules;
    private final Set<AbilityType> abilityTypes;
    private final Set<TargetingPattern> targetingPatterns;
    
    public SnapAbilityParser() {
        super();
        this.validValues = new HashMap<>();
        this.validCategories = new HashSet<>();
        this.validationRules = new HashSet<>();
        this.abilityTypes = new HashSet<>();
        this.targetingPatterns = new HashSet<>();
        
        validCategories.add(ComponentCategory.TARGET);
        initializeValidValues();
        initializeValidationRules();
        initializeAbilityTypes();
        initializeTargetingPatterns();
    }
    
    private void initializeValidValues() {
        validValues.put(ComponentCategory.BASE, new HashSet<>(Arrays.asList(
            "on_reveal", "ongoing", "move", "destroy", "add", "remove"
        )));
        
        validValues.put(ComponentCategory.TARGET, Set.of("card", "location", "player"));
        validValues.put(ComponentCategory.ACTION, Set.of("move", "destroy", "add_power", "reduce_power"));
        validValues.put(ComponentCategory.CONDITION, Set.of("if", "when", "after"));
        validValues.put(ComponentCategory.MODIFIER, Set.of("random", "all", "each"));
        
        validValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList(
            "random", "highest", "lowest", "adjacent", "opposite"
        )));
        
        validValues.put(ComponentCategory.EFFECT, new HashSet<>(Arrays.asList(
            "power", "cost", "ability", "text", "name"
        )));
    }

    private void initializeValidationRules() {
        validationRules.add(new ValidationRule(
            "Base Rule",
            "Every ability must have a base component",
            chain -> chain.getComponents().stream().anyMatch(c -> c.getCategory() == ComponentCategory.BASE),
            new HashSet<>(Arrays.asList(ComponentCategory.BASE)),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>()
        ));
        
        validationRules.add(new ValidationRule(
            "Target Rule",
            "Every ability must have a target component",
            chain -> chain.getComponents().stream().anyMatch(c -> c.getCategory() == ComponentCategory.TARGET),
            new HashSet<>(Arrays.asList(ComponentCategory.TARGET)),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>()
        ));
    }

    private void initializeAbilityTypes() {
        abilityTypes.add(new AbilityType(
            "On Reveal",
            "Abilities that trigger when a card is revealed",
            new HashSet<>(Arrays.asList(ComponentCategory.BASE, ComponentCategory.TARGET)),
            new HashSet<>(),
            validValues,
            validationRules,
            "on_reveal"
        ));
        
        abilityTypes.add(new AbilityType(
            "Ongoing",
            "Abilities that are active while the card is in play",
            new HashSet<>(Arrays.asList(ComponentCategory.BASE, ComponentCategory.TARGET)),
            new HashSet<>(),
            validValues,
            validationRules,
            "ongoing"
        ));
        
        abilityTypes.add(new AbilityType(
            "Trigger",
            "Abilities that trigger under specific conditions",
            new HashSet<>(Arrays.asList(ComponentCategory.BASE, ComponentCategory.TARGET)),
            new HashSet<>(),
            validValues,
            validationRules,
            "trigger"
        ));
    }

    private void initializeTargetingPatterns() {
        targetingPatterns.add(new TargetingPattern(
            "Basic Pattern",
            new HashSet<>(Arrays.asList(ComponentCategory.BASE, ComponentCategory.TARGET)),
            new HashSet<>(),
            validValues
        ));
    }
    
    @Override
    public TargetingChain parseAbility(String ability) {
        TargetingChain chain = new TargetingChain();
        
        String[] words = ability.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            
            // Check for base components
            if (validValues.get(ComponentCategory.BASE).contains(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.BASE, word));
                continue;
            }
            
            // Check for target components
            if (validValues.get(ComponentCategory.TARGET).contains(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, word));
                continue;
            }
            
            // Check for type components
            if (validValues.get(ComponentCategory.TYPE).contains(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, word));
                continue;
            }
            
            // Check for effect components
            if (validValues.get(ComponentCategory.EFFECT).contains(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, word));
                continue;
            }
        }
        
        return chain;
    }

    @Override
    public boolean isValidChain(TargetingChain chain) {
        return validationRules.stream().allMatch(rule -> rule.validate(chain));
    }

    @Override
    public String generateText(TargetingChain chain) {
        StringBuilder text = new StringBuilder();
        for (TargetingComponent component : chain.getComponents()) {
            text.append(component.getName()).append(" ");
        }
        return text.toString().trim();
    }

    @Override
    public Set<ComponentCategory> getValidCategories() {
        return validCategories;
    }

    @Override
    public Set<String> getValidComponents(ComponentCategory category) {
        return validValues.getOrDefault(category, new HashSet<>());
    }

    @Override
    public Map<ComponentCategory, Set<String>> getValidComponents() {
        return validValues;
    }

    @Override
    public Set<ValidationRule> getValidationRules() {
        return validationRules;
    }

    @Override
    public Set<AbilityType> getAbilityTypes() {
        return abilityTypes;
    }

    @Override
    public Set<TargetingPattern> getTargetingPatterns() {
        return targetingPatterns;
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