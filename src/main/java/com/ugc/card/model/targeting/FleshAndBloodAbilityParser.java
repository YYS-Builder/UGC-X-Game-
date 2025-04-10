package com.ugc.card.model.targeting;

import java.util.*;

public class FleshAndBloodAbilityParser implements GameAbilityParser {
    private static final Map<ComponentCategory, Set<String>> VALID_COMPONENTS = new HashMap<>();
    private static final Set<ValidationRule> VALIDATION_RULES = new HashSet<>();
    private static final Set<AbilityType> ABILITY_TYPES = new HashSet<>();
    private static final Set<TargetingPattern> TARGETING_PATTERNS = new HashSet<>();
    
    static {
        // Initialize valid components for each category
        VALID_COMPONENTS.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList(
            "Attack Action", "Defense Reaction", "Instant", "Arsenal", "Equipment"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.EFFECT, new HashSet<>(Arrays.asList(
            "deal damage", "gain life", "draw cards", "block", "destroy"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.DIRECTION, new HashSet<>(Arrays.asList(
            "to", "from", "at"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.VALUE, new HashSet<>(Arrays.asList(
            "1", "2", "3", "4", "5", "6", "X"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.STATE, new HashSet<>(Arrays.asList(
            "attacking", "defending", "ready", "exhausted"
        )));
        
        VALID_COMPONENTS.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList(
            "hero", "young hero", "weapon", "equipment", "arsenal", "graveyard"
        )));
        
        // Initialize validation rules
        Set<ComponentCategory> attackRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> attackForbiddenCategories = Collections.emptySet();
        Set<String> attackRequiredComponents = new HashSet<>(Arrays.asList("Attack Action"));
        Set<String> attackForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Attack Action Rule",
            "Attack actions must have an effect",
            chain -> true,
            attackRequiredCategories,
            attackForbiddenCategories,
            attackRequiredComponents,
            attackForbiddenComponents
        ));

        Set<ComponentCategory> defenseRequiredCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> defenseForbiddenCategories = Collections.emptySet();
        Set<String> defenseRequiredComponents = new HashSet<>(Arrays.asList("Defense Reaction"));
        Set<String> defenseForbiddenComponents = Collections.emptySet();
        
        VALIDATION_RULES.add(new ValidationRule(
            "Defense Reaction Rule",
            "Defense reactions must have a block effect",
            chain -> true,
            defenseRequiredCategories,
            defenseForbiddenCategories,
            defenseRequiredComponents,
            defenseForbiddenComponents
        ));
        
        // Initialize ability types
        Set<ComponentCategory> attackActionCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.VALUE
        ));
        Set<ComponentCategory> attackActionOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.TARGET, ComponentCategory.STATE
        ));
        Map<ComponentCategory, Set<String>> attackActionValidValues = new HashMap<>();
        attackActionValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Attack Action")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Attack Action",
            "Basic attack action ability",
            attackActionCategories,
            attackActionOptional,
            attackActionValidValues,
            VALIDATION_RULES,
            "{type} - {effect} {value} damage to {target}"
        ));

        Set<ComponentCategory> defenseReactionCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.VALUE
        ));
        Set<ComponentCategory> defenseReactionOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.TARGET, ComponentCategory.STATE
        ));
        Map<ComponentCategory, Set<String>> defenseReactionValidValues = new HashMap<>();
        defenseReactionValidValues.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Defense Reaction")));
        
        ABILITY_TYPES.add(new AbilityType(
            "Defense Reaction",
            "Basic defense reaction ability",
            defenseReactionCategories,
            defenseReactionOptional,
            defenseReactionValidValues,
            VALIDATION_RULES,
            "{type} - Block {value}"
        ));
        
        // Initialize targeting patterns
        Set<ComponentCategory> basicAttackCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.TARGET
        ));
        Set<ComponentCategory> basicAttackOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE
        ));
        Map<ComponentCategory, Set<String>> basicAttackValidComponents = new HashMap<>();
        basicAttackValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Attack Action")));
        basicAttackValidComponents.put(ComponentCategory.TARGET, new HashSet<>(Arrays.asList("hero", "young hero")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Basic Attack Pattern",
            basicAttackCategories,
            basicAttackOptional,
            basicAttackValidComponents
        ));

        Set<ComponentCategory> basicDefenseCategories = new HashSet<>(Arrays.asList(
            ComponentCategory.TYPE, ComponentCategory.EFFECT
        ));
        Set<ComponentCategory> basicDefenseOptional = new HashSet<>(Arrays.asList(
            ComponentCategory.VALUE, ComponentCategory.STATE
        ));
        Map<ComponentCategory, Set<String>> basicDefenseValidComponents = new HashMap<>();
        basicDefenseValidComponents.put(ComponentCategory.TYPE, new HashSet<>(Arrays.asList("Defense Reaction")));
        
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Basic Defense Pattern",
            basicDefenseCategories,
            basicDefenseOptional,
            basicDefenseValidComponents
        ));
    }
    
    private final Map<ComponentCategory, Set<String>> validValues;
    private final Set<ComponentCategory> validCategories;
    
    public FleshAndBloodAbilityParser() {
        this.validValues = new HashMap<>();
        this.validCategories = new HashSet<>();
        
        validCategories.add(ComponentCategory.TARGET);
        validCategories.add(ComponentCategory.ACTION);
        validCategories.add(ComponentCategory.CONDITION);
        validCategories.add(ComponentCategory.MODIFIER);
        
        validValues.put(ComponentCategory.TARGET, Set.of("hero", "weapon", "equipment"));
        validValues.put(ComponentCategory.ACTION, Set.of("attack", "block", "pitch", "draw"));
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