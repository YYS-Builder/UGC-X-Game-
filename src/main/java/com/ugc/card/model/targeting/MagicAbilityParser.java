package com.ugc.card.model.targeting;

import java.util.*;
import java.util.function.Predicate;

public class MagicAbilityParser implements GameAbilityParser {
    private static final Map<ComponentCategory, Set<String>> VALID_COMPONENTS = new HashMap<>();
    private static final Set<ValidationRule> VALIDATION_RULES = new HashSet<>();
    private static final Set<AbilityType> ABILITY_TYPES = new HashSet<>();
    private static final Set<TargetingPattern> TARGETING_PATTERNS = new HashSet<>();
    
    static {
        // Initialize valid components for each category
        VALID_COMPONENTS.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList(
            "Creature", "Instant", "Sorcery", "Enchantment", "Artifact", "Planeswalker", "Land"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.EFFECT, new HashSet<>(Arrays.asList(
            "deal damage", "gain life", "draw cards", "destroy", "exile", "counter", "return",
            "create token", "search library", "put into hand", "put into graveyard", "put into library",
            "tap", "untap", "gain control", "lose control", "gain ability", "lose ability"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.DIRECTION, new HashSet<>(Arrays.asList(
            "target", "each", "all", "any number of", "up to", "at least", "at most"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.VALUE, new HashSet<>(Arrays.asList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "X"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.STATE, new HashSet<>(Arrays.asList(
            "tapped", "untapped", "attacking", "blocking", "blocked", "unblocked", "destroyed",
            "exiled", "returned", "entered", "left", "died", "cast", "resolved"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList(
            "creature", "player", "planeswalker", "artifact", "enchantment", "land", "card",
            "permanent", "spell", "ability", "library", "hand", "graveyard", "exile"
        )));

        // Initialize validation rules
        Set<ComponentCategory> creatureEffectRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> creatureEffectForbiddenCategories = Collections.emptySet();
        Set<String> creatureEffectRequiredComponents = new HashSet<>(Arrays.asList("Creature"));
        Set<String> creatureEffectForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Creature Effect Rule",
            "Creature effects must have an effect",
            chain -> true,
            creatureEffectRequiredCategories,
            creatureEffectForbiddenCategories,
            creatureEffectRequiredComponents,
            creatureEffectForbiddenComponents
        ));

        Set<ComponentCategory> instantSorceryRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> instantSorceryForbiddenCategories = Collections.emptySet();
        Set<String> instantSorceryRequiredComponents = new HashSet<>(Arrays.asList("Instant", "Sorcery"));
        Set<String> instantSorceryForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Instant/Sorcery Rule",
            "Instants and sorceries must have an effect",
            chain -> true,
            instantSorceryRequiredCategories,
            instantSorceryForbiddenCategories,
            instantSorceryRequiredComponents,
            instantSorceryForbiddenComponents
        ));

        // Initialize ability types
        Set<ComponentCategory> creatureEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> creatureEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> creatureEffectValidValues = new HashMap<>();
        creatureEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Creature")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Creature Effect",
            "Basic creature effect ability",
            creatureEffectCategories,
            creatureEffectOptional,
            creatureEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        Set<ComponentCategory> instantEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> instantEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> instantEffectValidValues = new HashMap<>();
        instantEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Instant")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Instant Effect",
            "Basic instant effect ability",
            instantEffectCategories,
            instantEffectOptional,
            instantEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        Set<ComponentCategory> sorceryEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> sorceryEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> sorceryEffectValidValues = new HashMap<>();
        sorceryEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Sorcery")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Sorcery Effect",
            "Basic sorcery effect ability",
            sorceryEffectCategories,
            sorceryEffectOptional,
            sorceryEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        // Initialize targeting patterns
        Set<ComponentCategory> creatureEffectPatternCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> creatureEffectPatternOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> creatureEffectPatternValidComponents = new HashMap<>();
        creatureEffectPatternValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Creature")));
        creatureEffectPatternValidComponents.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList("creature", "player", "planeswalker")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Creature Effect Pattern",
            creatureEffectPatternCategories,
            creatureEffectPatternOptional,
            creatureEffectPatternValidComponents
        ));

        Set<ComponentCategory> instantSorceryPatternCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> instantSorceryPatternOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> instantSorceryPatternValidComponents = new HashMap<>();
        instantSorceryPatternValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Instant", "Sorcery")));
        instantSorceryPatternValidComponents.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList("creature", "player", "planeswalker", "spell")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Instant/Sorcery Pattern",
            instantSorceryPatternCategories,
            instantSorceryPatternOptional,
            instantSorceryPatternValidComponents
        ));
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
        // Check against all validation rules
        for (ValidationRule rule : VALIDATION_RULES) {
            if (!rule.validate(chain)) {
                return false;
            }
        }
        
        // Check if matches any ability type
        for (AbilityType abilityType : ABILITY_TYPES) {
            if (abilityType.isValid(chain)) {
                return true;
            }
        }
        
        // Basic validation for regular abilities
        return chain.getComponents().size() >= 2 &&
               chain.hasComponent(ComponentCategory.EFFECT) &&
               chain.hasComponent(ComponentCategory.TYPE);
    }
    
    @Override
    public String generateText(TargetingChain chain) {
        // Check if matches any ability type
        for (AbilityType abilityType : ABILITY_TYPES) {
            if (abilityType.isValid(chain)) {
                return generateKeywordText(chain, abilityType);
            }
        }
        
        // Generate text for regular ability
        return generateRegularText(chain);
    }
    
    private String generateKeywordText(TargetingChain chain, AbilityType abilityType) {
        String template = abilityType.getTemplate();
        for (ComponentCategory category : abilityType.getRequiredCategories()) {
            if (chain.hasComponent(category)) {
                template = template.replace("{" + category + "}", chain.getFirstComponentByCategory(category).getText());
            }
        }
        return template;
    }
    
    private String generateRegularText(TargetingChain chain) {
        StringBuilder text = new StringBuilder();
        
        // Add type if present
        if (chain.hasComponent(ComponentCategory.TYPE)) {
            text.append(chain.getFirstComponentByCategory(ComponentCategory.TYPE).getText()).append(" ");
        }
        
        // Add direction if present
        if (chain.hasComponent(ComponentCategory.DIRECTION)) {
            text.append(chain.getFirstComponentByCategory(ComponentCategory.DIRECTION).getText()).append(" ");
        }
        
        // Add effect if present
        if (chain.hasComponent(ComponentCategory.EFFECT)) {
            text.append(chain.getFirstComponentByCategory(ComponentCategory.EFFECT).getText()).append(" ");
        }
        
        // Add value if present
        if (chain.hasComponent(ComponentCategory.VALUE)) {
            text.append(chain.getFirstComponentByCategory(ComponentCategory.VALUE).getText()).append(" ");
        }
        
        // Add target if present
        if (chain.hasComponent(ComponentCategory.TARGET)) {
            text.append(chain.getFirstComponentByCategory(ComponentCategory.TARGET).getText());
        }
        
        return text.toString().trim();
    }
    
    @Override
    public Set<ComponentCategory> getValidCategories() {
        return new HashSet<>(VALID_COMPONENTS.keySet());
    }
    
    @Override
    public Set<String> getValidComponents(ComponentCategory category) {
        return new HashSet<>(VALID_COMPONENTS.getOrDefault(category, Collections.emptySet()));
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
    public Map<ComponentCategory, Set<String>> getValidComponents() {
        return new HashMap<>(VALID_COMPONENTS);
    }

    @Override
    public List<TargetingComponent> getComponents() {
        List<TargetingComponent> components = new ArrayList<>();
        for (Map.Entry<ComponentCategory, Set<String>> entry : VALID_COMPONENTS.entrySet()) {
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