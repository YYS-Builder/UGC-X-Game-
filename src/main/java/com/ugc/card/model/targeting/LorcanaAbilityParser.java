package com.ugc.card.model.targeting;

import java.util.*;
import java.util.stream.Collectors;

public class LorcanaAbilityParser implements GameAbilityParser {
    private static final Map<ComponentCategory, Set<String>> VALID_COMPONENTS = new HashMap<>();
    private static final Set<ValidationRule> VALIDATION_RULES = new HashSet<>();
    private static final Set<AbilityType> ABILITY_TYPES = new HashSet<>();
    private static final Set<TargetingPattern> TARGETING_PATTERNS = new HashSet<>();

    private final Map<ComponentCategory, Set<String>> validValues;
    private final Set<ComponentCategory> validCategories;
    
    public LorcanaAbilityParser() {
        this.validValues = new HashMap<>();
        this.validCategories = new HashSet<>();
        
        validCategories.add(ComponentCategory.TARGET);
        validCategories.add(ComponentCategory.ACTION);
        validCategories.add(ComponentCategory.CONDITION);
        validCategories.add(ComponentCategory.MODIFIER);
        
        validValues.put(ComponentCategory.TARGET, Set.of("character", "item", "location"));
        validValues.put(ComponentCategory.ACTION, Set.of("quest", "challenge", "lore", "ink"));
        validValues.put(ComponentCategory.CONDITION, Set.of("if", "when", "after"));
        validValues.put(ComponentCategory.MODIFIER, Set.of("all", "each", "random"));

        // Initialize valid components for each category
        VALID_COMPONENTS.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList(
            "Character", "Action", "Item", "Song", "Location"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.EFFECT, new HashSet<>(Arrays.asList(
            "draw", "banish", "return", "exert", "ready", "challenge", "quest",
            "gain lore", "lose lore", "gain strength", "lose strength", "gain willpower",
            "lose willpower", "gain evasion", "lose evasion", "gain ward", "lose ward"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.DIRECTION, new HashSet<>(Arrays.asList(
            "target", "each", "all", "any number of", "up to", "at least", "at most"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.VALUE, new HashSet<>(Arrays.asList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "X"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.STATE, new HashSet<>(Arrays.asList(
            "exerted", "ready", "challenged", "banished", "returned", "drawn",
            "played", "activated", "resolved"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList(
            "character", "action", "item", "song", "location", "card",
            "player", "deck", "hand", "discard", "banished"
        )));

        // Initialize validation rules
        Set<ComponentCategory> characterEffectRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> characterEffectForbiddenCategories = Collections.emptySet();
        Set<String> characterEffectRequiredComponents = new HashSet<>(Arrays.asList("Character"));
        Set<String> characterEffectForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Character Effect Rule",
            "Character effects must have an effect",
            chain -> true,
            characterEffectRequiredCategories,
            characterEffectForbiddenCategories,
            characterEffectRequiredComponents,
            characterEffectForbiddenComponents
        ));

        Set<ComponentCategory> actionEffectRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> actionEffectForbiddenCategories = Collections.emptySet();
        Set<String> actionEffectRequiredComponents = new HashSet<>(Arrays.asList("Action"));
        Set<String> actionEffectForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Action Effect Rule",
            "Action effects must have an effect",
            chain -> true,
            actionEffectRequiredCategories,
            actionEffectForbiddenCategories,
            actionEffectRequiredComponents,
            actionEffectForbiddenComponents
        ));

        // Initialize ability types
        Set<ComponentCategory> characterEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> characterEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> characterEffectValidValues = new HashMap<>();
        characterEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Character")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Character Effect",
            "Basic character effect ability",
            characterEffectCategories,
            characterEffectOptional,
            characterEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        Set<ComponentCategory> actionEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> actionEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> actionEffectValidValues = new HashMap<>();
        actionEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Action")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Action Effect",
            "Basic action effect ability",
            actionEffectCategories,
            actionEffectOptional,
            actionEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        // Initialize targeting patterns
        Set<ComponentCategory> characterEffectPatternCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> characterEffectPatternOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> characterEffectPatternValidComponents = new HashMap<>();
        characterEffectPatternValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Character")));
        characterEffectPatternValidComponents.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList("character", "player")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Character Effect Pattern",
            characterEffectPatternCategories,
            characterEffectPatternOptional,
            characterEffectPatternValidComponents
        ));

        Set<ComponentCategory> actionEffectPatternCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> actionEffectPatternOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> actionEffectPatternValidComponents = new HashMap<>();
        actionEffectPatternValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Action")));
        actionEffectPatternValidComponents.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList("character", "player", "card")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Action Effect Pattern",
            actionEffectPatternCategories,
            actionEffectPatternOptional,
            actionEffectPatternValidComponents
        ));
    }

    @Override
    public Set<ComponentCategory> getValidCategories() {
        return validCategories;
    }

    @Override
    public Set<String> getValidComponents(ComponentCategory category) {
        return validValues.getOrDefault(category, Collections.emptySet());
    }

    @Override
    public Set<ValidationRule> getValidationRules() {
        return new HashSet<>(VALIDATION_RULES);
    }

    @Override
    public Set<AbilityType> getAbilityTypes() {
        return new HashSet<>(ABILITY_TYPES);
    }

    @Override
    public Set<TargetingPattern> getTargetingPatterns() {
        return new HashSet<>(TARGETING_PATTERNS);
    }

    @Override
    public TargetingChain parseAbility(String abilityText) {
        TargetingChain chain = new TargetingChain();
        String[] parts = abilityText.split("\\s+");
        
        for (String part : parts) {
            for (Map.Entry<ComponentCategory, Set<String>> entry : VALID_COMPONENTS.entrySet()) {
                if (entry.getValue().contains(part)) {
                    chain.addComponent(new TargetingComponent(entry.getKey(), part));
                    break;
                }
            }
        }
        
        return chain;
    }

    @Override
    public boolean isValidChain(TargetingChain chain) {
        for (ValidationRule rule : VALIDATION_RULES) {
            if (!rule.validate(chain)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String generateText(TargetingChain chain) {
        StringBuilder text = new StringBuilder();
        List<TargetingComponent> components = chain.getComponents();
        
        for (int i = 0; i < components.size(); i++) {
            text.append(components.get(i).getValue());
            if (i < components.size() - 1) {
                text.append(" ");
            }
        }
        
        return text.toString();
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
    public Set<TargetingComponent> getComponentsAsSet() {
        Set<TargetingComponent> components = new HashSet<>();
        for (Map.Entry<ComponentCategory, Set<String>> entry : VALID_COMPONENTS.entrySet()) {
            for (String value : entry.getValue()) {
                components.add(new TargetingComponent(entry.getKey(), value));
            }
        }
        return components;
    }

    @Override
    public Map<ComponentCategory, Set<String>> getValidComponents() {
        return new HashMap<>(VALID_COMPONENTS);
    }

    @Override
    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        return new ArrayList<>(getComponentsByCategory(category));
    }

    @Override
    public Set<TargetingComponent> getComponentsByCategory(ComponentCategory category) {
        Set<String> values = getValidComponents(category);
        return convertToComponents(values, category);
    }

    @Override
    public Set<TargetingComponent> convertToComponents(Set<String> values, ComponentCategory category) {
        return values.stream()
            .map(value -> new TargetingComponent(category, value))
            .collect(Collectors.toSet());
    }

    @Override
    public List<TargetingComponent> convertToComponentList(Set<String> values, ComponentCategory category) {
        return new ArrayList<>(convertToComponents(values, category));
    }
} 