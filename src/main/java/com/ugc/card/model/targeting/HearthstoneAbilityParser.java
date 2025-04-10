package com.ugc.card.model.targeting;

import java.util.*;
import java.util.regex.*;

public class HearthstoneAbilityParser implements GameAbilityParser {
    private static final Map<String, ComponentCategory> KEYWORD_CATEGORIES = new HashMap<>();
    private static final Map<String, ComponentCategory> EFFECT_CATEGORIES = new HashMap<>();
    private static final Map<String, ComponentCategory> DIRECTION_CATEGORIES = new HashMap<>();
    private static final Map<String, ComponentCategory> STATE_CATEGORIES = new HashMap<>();
    private static final Set<ValidationRule> VALIDATION_RULES = new HashSet<>();
    private static final Set<AbilityType> ABILITY_TYPES = new HashSet<>();
    private static final Set<TargetingPattern> TARGETING_PATTERNS = new HashSet<>();
    
    private TargetingSystem targetingSystem;
    private AbilityGenerator abilityGenerator;
    
    private final Map<ComponentCategory, Set<String>> validValues;
    private final Set<ComponentCategory> validCategories;
    
    static {
        // Initialize keyword mappings
        KEYWORD_CATEGORIES.put("battlecry", ComponentCategory.TRIGGER);
        KEYWORD_CATEGORIES.put("deathrattle", ComponentCategory.TRIGGER);
        KEYWORD_CATEGORIES.put("at the end of", ComponentCategory.TRIGGER);
        KEYWORD_CATEGORIES.put("at the start of", ComponentCategory.TRIGGER);
        KEYWORD_CATEGORIES.put("if", ComponentCategory.CONDITION);
        
        // Initialize effect mappings
        EFFECT_CATEGORIES.put("deal", ComponentCategory.EFFECT);
        EFFECT_CATEGORIES.put("restore", ComponentCategory.EFFECT);
        EFFECT_CATEGORIES.put("draw", ComponentCategory.EFFECT);
        EFFECT_CATEGORIES.put("gain", ComponentCategory.EFFECT);
        EFFECT_CATEGORIES.put("summon", ComponentCategory.EFFECT);
        EFFECT_CATEGORIES.put("discover", ComponentCategory.EFFECT);
        
        // Initialize direction mappings
        DIRECTION_CATEGORIES.put("target", ComponentCategory.DIRECTION);
        DIRECTION_CATEGORIES.put("random", ComponentCategory.DIRECTION);
        DIRECTION_CATEGORIES.put("all", ComponentCategory.DIRECTION);
        DIRECTION_CATEGORIES.put("adjacent", ComponentCategory.DIRECTION);
        
        // Initialize state mappings
        STATE_CATEGORIES.put("damaged", ComponentCategory.STATE);
        STATE_CATEGORIES.put("frozen", ComponentCategory.STATE);
        STATE_CATEGORIES.put("silenced", ComponentCategory.STATE);
        STATE_CATEGORIES.put("poisonous", ComponentCategory.STATE);

        // Initialize validation rules
        VALIDATION_RULES.add(new ValidationRule(
            "Battlecry Rule",
            "Battlecry effects must have an effect",
            chain -> {
                if (chain.hasComponent(ComponentCategory.TRIGGER, "battlecry")) {
                    return chain.hasComponentOfCategory(ComponentCategory.EFFECT);
                }
                return true;
            },
            new HashSet<>(Arrays.asList(ComponentCategory.TRIGGER, ComponentCategory.EFFECT)),
            Collections.emptySet(),
            new HashSet<>(Arrays.asList("battlecry")),
            Collections.emptySet()
        ));

        VALIDATION_RULES.add(new ValidationRule(
            "Deathrattle Rule",
            "Deathrattle effects must have an effect",
            chain -> {
                if (chain.hasComponent(ComponentCategory.TRIGGER, "deathrattle")) {
                    return chain.hasComponentOfCategory(ComponentCategory.EFFECT);
                }
                return true;
            },
            new HashSet<>(Arrays.asList(ComponentCategory.TRIGGER, ComponentCategory.EFFECT)),
            Collections.emptySet(),
            new HashSet<>(Arrays.asList("deathrattle")),
            Collections.emptySet()
        ));

        // Initialize ability types
        ABILITY_TYPES.add(new AbilityType(
            "Battlecry",
            "An effect that triggers when the minion is played",
            new HashSet<>(Arrays.asList(ComponentCategory.TRIGGER, ComponentCategory.EFFECT)),
            new HashSet<>(Arrays.asList(ComponentCategory.DIRECTION, ComponentCategory.VALUE, ComponentCategory.TYPE)),
            Map.of(
                ComponentCategory.TRIGGER, new HashSet<>(Arrays.asList("battlecry")),
                ComponentCategory.EFFECT, new HashSet<>(EFFECT_CATEGORIES.keySet()),
                ComponentCategory.DIRECTION, new HashSet<>(DIRECTION_CATEGORIES.keySet()),
                ComponentCategory.TYPE, new HashSet<>(Arrays.asList("minion", "spell", "weapon", "hero", "secret", "card"))
            ),
            VALIDATION_RULES,
            "{TRIGGER}: {EFFECT} {VALUE} {TYPE} {DIRECTION}"
        ));

        ABILITY_TYPES.add(new AbilityType(
            "Deathrattle",
            "An effect that triggers when the minion dies",
            new HashSet<>(Arrays.asList(ComponentCategory.TRIGGER, ComponentCategory.EFFECT)),
            new HashSet<>(Arrays.asList(ComponentCategory.DIRECTION, ComponentCategory.VALUE, ComponentCategory.TYPE)),
            Map.of(
                ComponentCategory.TRIGGER, new HashSet<>(Arrays.asList("deathrattle")),
                ComponentCategory.EFFECT, new HashSet<>(EFFECT_CATEGORIES.keySet()),
                ComponentCategory.DIRECTION, new HashSet<>(DIRECTION_CATEGORIES.keySet()),
                ComponentCategory.TYPE, new HashSet<>(Arrays.asList("minion", "spell", "weapon", "hero", "secret", "card"))
            ),
            VALIDATION_RULES,
            "{TRIGGER}: {EFFECT} {VALUE} {TYPE} {DIRECTION}"
        ));

        // Initialize targeting patterns
        TARGETING_PATTERNS.add(new TargetingPattern(
            "Battlecry Pattern",
            new HashSet<>(Arrays.asList(ComponentCategory.TRIGGER, ComponentCategory.EFFECT)),
            new HashSet<>(Arrays.asList(ComponentCategory.DIRECTION, ComponentCategory.VALUE, ComponentCategory.TYPE)),
            Map.of(
                ComponentCategory.TRIGGER, new HashSet<>(Arrays.asList("battlecry")),
                ComponentCategory.EFFECT, new HashSet<>(EFFECT_CATEGORIES.keySet()),
                ComponentCategory.DIRECTION, new HashSet<>(DIRECTION_CATEGORIES.keySet()),
                ComponentCategory.TYPE, new HashSet<>(Arrays.asList("minion", "spell", "weapon", "hero", "secret", "card"))
            )
        ));

        TARGETING_PATTERNS.add(new TargetingPattern(
            "Deathrattle Pattern",
            new HashSet<>(Arrays.asList(ComponentCategory.TRIGGER, ComponentCategory.EFFECT)),
            new HashSet<>(Arrays.asList(ComponentCategory.DIRECTION, ComponentCategory.VALUE, ComponentCategory.TYPE)),
            Map.of(
                ComponentCategory.TRIGGER, new HashSet<>(Arrays.asList("deathrattle")),
                ComponentCategory.EFFECT, new HashSet<>(EFFECT_CATEGORIES.keySet()),
                ComponentCategory.DIRECTION, new HashSet<>(DIRECTION_CATEGORIES.keySet()),
                ComponentCategory.TYPE, new HashSet<>(Arrays.asList("minion", "spell", "weapon", "hero", "secret", "card"))
            )
        ));
    }
    
    public HearthstoneAbilityParser(TargetingSystem targetingSystem) {
        this.targetingSystem = targetingSystem;
        this.abilityGenerator = new AbilityGenerator(targetingSystem);
        this.validValues = new HashMap<>();
        this.validCategories = new HashSet<>();
        
        validCategories.add(ComponentCategory.TARGET);
        validCategories.add(ComponentCategory.ACTION);
        validCategories.add(ComponentCategory.CONDITION);
        validCategories.add(ComponentCategory.MODIFIER);
        
        validValues.put(ComponentCategory.TARGET, Set.of("minion", "hero", "spell"));
        validValues.put(ComponentCategory.ACTION, Set.of("attack", "heal", "damage", "summon"));
        validValues.put(ComponentCategory.CONDITION, Set.of("if", "when", "after"));
        validValues.put(ComponentCategory.MODIFIER, Set.of("all", "each", "random"));
        
        initializeValidValues();
        initializeValidationRules();
        initializeAbilityTypes();
        initializeTargetingPatterns();
    }
    
    @Override
    public TargetingChain parseAbility(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new TargetingChain();
        }
        
        text = text.toLowerCase().trim();
        TargetingChain chain = new TargetingChain();
        
        // Check for keyword abilities first
        if (isKeywordAbility(text)) {
            return parseKeywordAbility(text);
        }
        
        // Split the text into words
        String[] words = text.split("\\s+");
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String phrase = getPhrase(words, i, 3);  // Look ahead up to 3 words
            
            // Check for multi-word keywords
            ComponentCategory category = KEYWORD_CATEGORIES.get(phrase);
            if (category != null) {
                chain.addComponent(new TargetingComponent(category, phrase));
                i += phrase.split("\\s+").length - 1;
                continue;
            }
            
            // Check for single word categories
            category = KEYWORD_CATEGORIES.get(word);
            if (category != null) {
                chain.addComponent(new TargetingComponent(category, word));
                continue;
            }
            
            // Check for effects
            category = EFFECT_CATEGORIES.get(word);
            if (category != null) {
                chain.addComponent(new TargetingComponent(category, word));
                continue;
            }
            
            // Check for directions
            category = DIRECTION_CATEGORIES.get(word);
            if (category != null) {
                chain.addComponent(new TargetingComponent(category, word));
                continue;
            }
            
            // Check for states
            category = STATE_CATEGORIES.get(word);
            if (category != null) {
                chain.addComponent(new TargetingComponent(category, word));
                continue;
            }
            
            // Check for numbers (values)
            if (word.matches("\\d+")) {
                chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, word));
                continue;
            }
            
            // Check for types
            if (isType(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, word));
            }
        }
        
        return chain;
    }
    
    private String getPhrase(String[] words, int start, int maxWords) {
        StringBuilder phrase = new StringBuilder();
        for (int i = 0; i < maxWords && start + i < words.length; i++) {
            if (i > 0) phrase.append(" ");
            phrase.append(words[start + i]);
            
            // Check if this phrase exists in our mappings
            if (KEYWORD_CATEGORIES.containsKey(phrase.toString())) {
                return phrase.toString();
            }
        }
        return "";
    }
    
    private boolean isKeywordAbility(String text) {
        return text.startsWith("battlecry:") || 
               text.startsWith("deathrattle:") ||
               text.startsWith("inspire:") ||
               text.startsWith("combo:") ||
               text.startsWith("spellburst:");
    }
    
    private TargetingChain parseKeywordAbility(String text) {
        TargetingChain chain = new TargetingChain();
        String[] parts = text.split(":", 2);
        
        // Add the keyword as a trigger
        chain.addComponent(new TargetingComponent(ComponentCategory.TRIGGER, parts[0].trim()));
        
        // Parse the rest of the ability if there is more
        if (parts.length > 1) {
            TargetingChain effectChain = parseAbility(parts[1].trim());
            chain.getComponents().addAll(effectChain.getComponents());
        }
        
        return chain;
    }
    
    private boolean isType(String word) {
        Set<String> validTypes = new HashSet<>(Arrays.asList(
            "minion", "spell", "weapon", "hero", "secret", "card"
        ));
        return validTypes.contains(word);
    }
    
    @Override
    public boolean isValidChain(TargetingChain chain) {
        // Implement Hearthstone-specific validation rules
        if (chain == null || chain.getComponents().isEmpty()) {
            return false;
        }
        
        // Must have either a trigger or an effect
        boolean hasTrigger = chain.hasComponentOfCategory(ComponentCategory.TRIGGER);
        boolean hasEffect = chain.hasComponentOfCategory(ComponentCategory.EFFECT);
        if (!hasTrigger && !hasEffect) {
            return false;
        }
        
        // If it has a numerical effect, it must have a value
        if (hasNumericalEffect(chain) && !chain.hasComponentOfCategory(ComponentCategory.VALUE)) {
            return false;
        }
        
        // If it has a target direction, it must have a type
        if (hasTargetDirection(chain) && !chain.hasComponentOfCategory(ComponentCategory.TYPE)) {
            return false;
        }
        
        return true;
    }
    
    private boolean hasNumericalEffect(TargetingChain chain) {
        if (!chain.hasComponentOfCategory(ComponentCategory.EFFECT)) {
            return false;
        }
        
        String effect = chain.getFirstComponentByCategory(ComponentCategory.EFFECT).getText();
        return effect.equals("deal") || effect.equals("restore") || effect.equals("draw");
    }
    
    private boolean hasTargetDirection(TargetingChain chain) {
        if (!chain.hasComponentOfCategory(ComponentCategory.DIRECTION)) {
            return false;
        }
        
        String direction = chain.getFirstComponentByCategory(ComponentCategory.DIRECTION).getText();
        return direction.equals("target");
    }
    
    @Override
    public String generateText(TargetingChain chain) {
        return abilityGenerator.generateText(chain);
    }
    
    @Override
    public Set<ComponentCategory> getValidCategories() {
        return new HashSet<>(Arrays.asList(
            ComponentCategory.TRIGGER,
            ComponentCategory.EFFECT,
            ComponentCategory.DIRECTION,
            ComponentCategory.VALUE,
            ComponentCategory.TYPE,
            ComponentCategory.STATE,
            ComponentCategory.CONDITION
        ));
    }
    
    @Override
    public Set<String> getValidComponents(ComponentCategory category) {
        switch (category) {
            case TRIGGER:
                return new HashSet<>(KEYWORD_CATEGORIES.keySet());
            case EFFECT:
                return new HashSet<>(EFFECT_CATEGORIES.keySet());
            case DIRECTION:
                return new HashSet<>(DIRECTION_CATEGORIES.keySet());
            case STATE:
                return new HashSet<>(STATE_CATEGORIES.keySet());
            case TYPE:
                return new HashSet<>(Arrays.asList(
                    "minion", "spell", "weapon", "hero", "secret", "card"
                ));
            default:
                return Collections.emptySet();
        }
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
        Map<ComponentCategory, Set<String>> result = new HashMap<>();
        result.putAll(validValues);
        result.put(ComponentCategory.TRIGGER, new HashSet<>(KEYWORD_CATEGORIES.keySet()));
        result.put(ComponentCategory.EFFECT, new HashSet<>(EFFECT_CATEGORIES.keySet()));
        result.put(ComponentCategory.DIRECTION, new HashSet<>(DIRECTION_CATEGORIES.keySet()));
        result.put(ComponentCategory.STATE, new HashSet<>(STATE_CATEGORIES.keySet()));
        return result;
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

    private void initializeValidValues() {
        // Implementation of initializeValidValues method
    }

    private void initializeValidationRules() {
        // Implementation of initializeValidationRules method
    }

    private void initializeAbilityTypes() {
        // Implementation of initializeAbilityTypes method
    }

    private void initializeTargetingPatterns() {
        // Implementation of initializeTargetingPatterns method
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