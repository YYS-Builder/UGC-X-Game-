package com.ugc.card.model.targeting;

import java.util.*;
import java.util.stream.Collectors;

public class YugiohAbilityParser implements GameAbilityParser {
    private static final Map<ComponentCategory, Set<String>> VALID_COMPONENTS = new HashMap<>();
    private static final Set<ValidationRule> VALIDATION_RULES = new HashSet<>();
    private static final Set<AbilityType> ABILITY_TYPES = new HashSet<>();
    private static final Set<TargetingPattern> TARGETING_PATTERNS = new HashSet<>();
    
    private final Map<ComponentCategory, Set<String>> validValues;
    private final Set<ComponentCategory> validCategories;
    
    public YugiohAbilityParser() {
        this.validValues = new HashMap<>();
        this.validCategories = new HashSet<>();
        
        validCategories.add(ComponentCategory.TARGET);
        validCategories.add(ComponentCategory.ACTION);
        validCategories.add(ComponentCategory.CONDITION);
        validCategories.add(ComponentCategory.MODIFIER);
        
        validValues.put(ComponentCategory.TARGET, Set.of("monster", "spell", "trap"));
        validValues.put(ComponentCategory.ACTION, Set.of("summon", "activate", "set", "tribute"));
        validValues.put(ComponentCategory.CONDITION, Set.of("if", "when", "after"));
        validValues.put(ComponentCategory.MODIFIER, Set.of("all", "each", "random"));
        
        // Initialize valid components for each category
        VALID_COMPONENTS.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList(
            "Monster Effect", "Spell", "Trap", "Normal", "Quick-Play", "Continuous", "Equip", "Field"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.EFFECT, new HashSet<>(Arrays.asList(
            "destroy", "banish", "send to graveyard", "add to hand", "special summon", "draw", "search",
            "negate", "change position", "change control", "gain attack", "lose attack", "gain defense",
            "lose defense", "cannot be destroyed", "cannot be targeted", "cannot be tributed"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.DIRECTION, new HashSet<>(Arrays.asList(
            "your", "opponent", "target", "all", "each", "this", "that"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.VALUE, new HashSet<>(Arrays.asList(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "X"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.STATE, new HashSet<>(Arrays.asList(
            "face-up", "face-down", "attack position", "defense position", "set", "activated", "resolved"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList(
            "monster", "spell", "trap", "card", "deck", "hand", "graveyard", "banished", "field"
        )));

        // Initialize validation rules
        Set<ComponentCategory> monsterEffectRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> monsterEffectForbiddenCategories = Collections.emptySet();
        Set<String> monsterEffectRequiredComponents = new HashSet<>(Arrays.asList("Monster Effect"));
        Set<String> monsterEffectForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Monster Effect Rule",
            "Monster effects must have an effect",
            chain -> true,
            monsterEffectRequiredCategories,
            monsterEffectForbiddenCategories,
            monsterEffectRequiredComponents,
            monsterEffectForbiddenComponents
        ));

        Set<ComponentCategory> spellTrapRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> spellTrapForbiddenCategories = Collections.emptySet();
        Set<String> spellTrapRequiredComponents = new HashSet<>(Arrays.asList("Spell", "Trap"));
        Set<String> spellTrapForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Spell/Trap Rule",
            "Spells and traps must have an effect",
            chain -> true,
            spellTrapRequiredCategories,
            spellTrapForbiddenCategories,
            spellTrapRequiredComponents,
            spellTrapForbiddenComponents
        ));

        // Initialize ability types
        Set<ComponentCategory> monsterEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> monsterEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> monsterEffectValidValues = new HashMap<>();
        monsterEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Monster Effect")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Monster Effect",
            "Basic monster effect ability",
            monsterEffectCategories,
            monsterEffectOptional,
            monsterEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        Set<ComponentCategory> spellEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> spellEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> spellEffectValidValues = new HashMap<>();
        spellEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Spell")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Spell Effect",
            "Basic spell effect ability",
            spellEffectCategories,
            spellEffectOptional,
            spellEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        Set<ComponentCategory> trapEffectCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> trapEffectOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> trapEffectValidValues = new HashMap<>();
        trapEffectValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Trap")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Trap Effect",
            "Basic trap effect ability",
            trapEffectCategories,
            trapEffectOptional,
            trapEffectValidValues,
            VALIDATION_RULES,
            "{type}: {effect} {target}"
        ));

        // Initialize targeting patterns
        Set<ComponentCategory> monsterEffectPatternCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> monsterEffectPatternOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> monsterEffectPatternValidComponents = new HashMap<>();
        monsterEffectPatternValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Monster Effect")));
        monsterEffectPatternValidComponents.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList("monster", "card")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Monster Effect Pattern",
            monsterEffectPatternCategories,
            monsterEffectPatternOptional,
            monsterEffectPatternValidComponents
        ));

        Set<ComponentCategory> spellTrapPatternCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> spellTrapPatternOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE, ComponentCategory.DIRECTION
        ));
        Map<ComponentCategory, Set<String>> spellTrapPatternValidComponents = new HashMap<>();
        spellTrapPatternValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Spell", "Trap")));
        spellTrapPatternValidComponents.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList("monster", "card")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Spell/Trap Pattern",
            spellTrapPatternCategories,
            spellTrapPatternOptional,
            spellTrapPatternValidComponents
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
    public Map<ComponentCategory, Set<String>> getValidComponents() {
        return new HashMap<>(VALID_COMPONENTS);
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