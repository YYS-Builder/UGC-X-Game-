package com.ugc.card.model.targeting;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Interface defining the contract for game-specific ability parsers.
 * Each game implementation should provide its own parser that understands
 * the specific syntax and rules of that game's abilities.
 */
public interface GameAbilityParser {
    /**
     * Parses ability text into a targeting chain.
     * 
     * @param text The ability text to parse
     * @return A TargetingChain representing the parsed ability
     */
    TargetingChain parseAbility(String text);

    /**
     * Validates whether a targeting chain is valid according to the game's rules.
     * 
     * @param chain The targeting chain to validate
     * @return true if the chain is valid, false otherwise
     */
    boolean isValidChain(TargetingChain chain);

    /**
     * Generates ability text from a targeting chain.
     * 
     * @param chain The targeting chain to generate text from
     * @return The generated ability text
     */
    String generateText(TargetingChain chain);

    /**
     * Gets all valid component categories for this game.
     * 
     * @return A set of valid ComponentCategory values
     */
    Set<ComponentCategory> getValidCategories();

    /**
     * Gets all valid component values for a given category in this game.
     * 
     * @param category The category to get valid components for
     * @return A set of valid component values for the category
     */
    Set<String> getValidComponents(ComponentCategory category);

    // Get game-specific validation rules
    Set<ValidationRule> getValidationRules();
    
    // Get game-specific ability types
    Set<AbilityType> getAbilityTypes();
    
    // Get game-specific targeting patterns
    Set<TargetingPattern> getTargetingPatterns();

    List<TargetingComponent> getComponents();

    Map<ComponentCategory, Set<String>> getValidComponents();

    public Set<TargetingComponent> convertToComponents(Set<String> values, ComponentCategory category);

    public List<TargetingComponent> convertToComponentList(Set<String> values, ComponentCategory category);

    public Set<TargetingComponent> getComponentsByCategory(ComponentCategory category);

    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category);
} 