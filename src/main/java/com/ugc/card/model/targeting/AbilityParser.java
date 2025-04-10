package com.ugc.card.model.targeting;

import java.util.*;
import java.util.regex.*;

public interface AbilityParser {
    Set<String> getValidComponents(ComponentCategory category);
    Set<ComponentCategory> getValidCategories();
    List<TargetingComponent> getComponents();
    TargetingChain parseAbility(String abilityText);
    
    Set<ValidationRule> getValidationRules();
    Set<AbilityType> getAbilityTypes();
    Set<TargetingPattern> getTargetingPatterns();
    
    Map<ComponentCategory, Set<String>> getValidComponents();
    
    boolean isValidChain(TargetingChain chain);
    String generateText(TargetingChain chain);
} 