package com.ugc.card.model.targeting;

import java.util.*;
import java.util.function.Predicate;

public class AbilityValidator {
    private final List<ValidationRule> rules;
    private final Map<String, Predicate<TargetingChain>> customValidators;
    private final TargetingSystem targetingSystem;
    private final Map<String, Set<TargetingChain>> validAbilities;
    private final Set<ComponentCategory> requiredCategories;
    private final Set<ComponentCategory> optionalCategories;
    private final Set<TargetingComponent> validComponents;
    
    public AbilityValidator(TargetingSystem targetingSystem) {
        this.targetingSystem = targetingSystem;
        this.rules = new ArrayList<>();
        this.customValidators = new HashMap<>();
        this.validAbilities = new HashMap<>();
        this.requiredCategories = new HashSet<>();
        this.optionalCategories = new HashSet<>();
        this.validComponents = new HashSet<>();
        initializeDefaultRules();
        initializeCustomValidators();
    }
    
    private void initializeDefaultRules() {
        // Effect must have a value if it's a numerical effect
        rules.add(new ValidationRule(
            "Effect Value Rule",
            "Numerical effects must have a value",
            chain -> {
                if (chain.hasComponentOfCategory(ComponentCategory.EFFECT)) {
                    String effect = chain.getFirstComponentByCategory(ComponentCategory.EFFECT).getText();
                    if (isNumericalEffect(effect)) {
                        return chain.hasComponentOfCategory(ComponentCategory.VALUE);
                    }
                }
                return true;
            },
            new HashSet<>(Arrays.asList(ComponentCategory.EFFECT)),
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet()
        ));
        
        // Target direction must have a valid type
        rules.add(new ValidationRule(
            "Target Type Rule",
            "Target direction must specify a valid type",
            chain -> {
                if (chain.hasComponentOfCategory(ComponentCategory.DIRECTION)) {
                    String direction = chain.getFirstComponentByCategory(ComponentCategory.DIRECTION).getText();
                    if (direction.equals("target")) {
                        return chain.hasComponentOfCategory(ComponentCategory.TYPE);
                    }
                }
                return true;
            },
            new HashSet<>(Arrays.asList(ComponentCategory.DIRECTION)),
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet()
        ));
        
        // Trigger must have an effect
        rules.add(new ValidationRule(
            "Trigger Effect Rule",
            "Trigger abilities must have an effect",
            chain -> {
                if (chain.hasComponentOfCategory(ComponentCategory.TRIGGER)) {
                    return chain.hasComponentOfCategory(ComponentCategory.EFFECT);
                }
                return true;
            },
            new HashSet<>(Arrays.asList(ComponentCategory.TRIGGER)),
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet()
        ));
    }
    
    private void initializeCustomValidators() {
        // Add custom validators for specific ability patterns
        customValidators.put("damage", chain -> {
            if (!chain.hasComponentOfCategory(ComponentCategory.VALUE)) {
                return false;
            }
            String value = chain.getFirstComponentByCategory(ComponentCategory.VALUE).getText();
            try {
                int damage = Integer.parseInt(value);
                return damage > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        
        customValidators.put("draw", chain -> {
            if (!chain.hasComponentOfCategory(ComponentCategory.VALUE)) {
                return false;
            }
            String value = chain.getFirstComponentByCategory(ComponentCategory.VALUE).getText();
            try {
                int cards = Integer.parseInt(value);
                return cards > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }
    
    private boolean isNumericalEffect(String effect) {
        Set<String> numericalEffects = new HashSet<>(Arrays.asList(
            "deal", "draw", "gain", "lose", "add", "remove"
        ));
        return numericalEffects.contains(effect.toLowerCase());
    }
    
    public boolean validate(TargetingChain chain) {
        // Check all default rules
        for (ValidationRule rule : rules) {
            if (!rule.validate(chain)) {
                return false;
            }
        }
        
        // Check if chain matches any patterns and validate against custom validators
        for (String pattern : AbilityPatterns.getAvailablePatterns()) {
            if (AbilityPatterns.matchesPattern(chain, pattern)) {
                Predicate<TargetingChain> validator = customValidators.get(pattern);
                if (validator != null && !validator.test(chain)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public List<String> getValidationErrors(TargetingChain chain) {
        List<String> errors = new ArrayList<>();
        
        // Check default rules
        for (ValidationRule rule : rules) {
            if (!rule.validate(chain)) {
                errors.add(rule.getDescription());
            }
        }
        
        // Check pattern-specific validators
        for (String pattern : AbilityPatterns.getAvailablePatterns()) {
            if (AbilityPatterns.matchesPattern(chain, pattern)) {
                Predicate<TargetingChain> validator = customValidators.get(pattern);
                if (validator != null && !validator.test(chain)) {
                    errors.add("Invalid " + pattern + " pattern");
                }
            }
        }
        
        return errors;
    }
    
    public void addRule(ValidationRule rule) {
        rules.add(rule);
    }
    
    public void addCustomValidator(String pattern, Predicate<TargetingChain> validator) {
        customValidators.put(pattern, validator);
    }
    
    public List<ValidationRule> getRules() {
        return Collections.unmodifiableList(rules);
    }
    
    public Set<String> getCustomValidatorPatterns() {
        return Collections.unmodifiableSet(customValidators.keySet());
    }
    
    public void addValidAbility(String ability, Set<TargetingChain> chains) {
        validAbilities.put(ability, chains);
    }
    
    public boolean validateAbility(String abilityText, String game) {
        GameAbilityParser parser = targetingSystem.getGameParser(game);
        if (parser == null) {
            return false;
        }
        
        TargetingChain chain = parser.parseAbility(abilityText);
        // Check if the chain matches a valid ability
        for (Set<TargetingChain> validChains : validAbilities.values()) {
            for (TargetingChain validChain : validChains) {
                if (chain.equals(validChain)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public Set<String> getValidAbilities() {
        return validAbilities.keySet();
    }
    
    public int getValidAbilityCount() {
        return validAbilities.size();
    }

    public void addRequiredCategory(ComponentCategory category) {
        requiredCategories.add(category);
    }

    public void addOptionalCategory(ComponentCategory category) {
        optionalCategories.add(category);
    }

    public void addValidComponent(TargetingComponent component) {
        validComponents.add(component);
    }

    public boolean validateChain(TargetingChain chain) {
        Set<ComponentCategory> presentCategories = new HashSet<>();
        Set<TargetingComponent> components = chain.getComponents();

        // Check if all required categories are present
        for (TargetingComponent component : components) {
            presentCategories.add(component.getCategory());
            if (!validComponents.contains(component)) {
                return false;
            }
        }

        for (ComponentCategory required : requiredCategories) {
            if (!presentCategories.contains(required)) {
                return false;
            }
        }

        // Check if all present categories are either required or optional
        for (ComponentCategory present : presentCategories) {
            if (!requiredCategories.contains(present) && !optionalCategories.contains(present)) {
                return false;
            }
        }

        return true;
    }

    public Set<TargetingChain> validateChains(Set<TargetingChain> chains) {
        Set<TargetingChain> validChains = new HashSet<>();
        for (TargetingChain chain : chains) {
            if (validateChain(chain)) {
                validChains.add(chain);
            }
        }
        return validChains;
    }
} 