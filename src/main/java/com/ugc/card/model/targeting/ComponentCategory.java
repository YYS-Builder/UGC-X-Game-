package com.ugc.card.model.targeting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum ComponentCategory {
    // Core categories
    BASE,       // Base component for all abilities
    TYPE,       // The type of entity (minion, spell, weapon, etc.)
    EFFECT,     // The action being performed (deal, draw, heal, etc.)
    DIRECTION,  // The targeting direction (target, random, all, etc.)
    VALUE,      // Numerical values (1, 2, 3, etc.)
    STATE,      // Entity states (damaged, frozen, silenced, etc.)
    TARGET,     // Specific targets (hero, minion, character, etc.)
    
    // Modifiers and conditions
    MODIFIER,   // Modifiers for effects (additional, twice, etc.)
    CONDITION,  // Conditions for effects (if, when, whenever, etc.)
    TRIGGER,    // Trigger conditions (battlecry, deathrattle, etc.)
    TIMING,     // Timing specifications (at end of turn, at start of game, etc.)
    
    // Resource related
    COST,       // Resource costs (mana, energy, life, etc.)
    RESOURCE,   // Resource types (mana crystal, energy point, etc.)
    PITCH_VALUE,// Pitch value for Flesh and Blood
    ENERGY,     // Energy value for Marvel Snap
    
    // Game zones
    ZONE,       // Game zones (hand, deck, battlefield, etc.)
    LOCATION,   // Specific locations (adjacent, leftmost, rightmost, etc.)
    POSITION,   // Position on the battlefield
    
    // Ownership and control
    OWNER,      // Ownership specifications (your, opponent's, etc.)
    CONTROL,    // Control specifications (friendly, enemy, any, etc.)
    
    // Special categories
    KEYWORD,    // Game keywords (taunt, divine shield, etc.)
    COUNTER,    // Counter types (charge counter, time counter, etc.)
    TAG,        // Special tags (legendary, beast, dragon, etc.)
    SPECIAL,    // Special cases and unique effects
    CHAIN_LINK, // Chain link for Flesh and Blood
    FORCE,      // Force alignment for Star Wars
    DAMAGE,     // Damage value
    ON_CARD,    // On-card effects
    RESULT,     // Result of an ability
    INK,        // For Lorcana
    ACTION;     // Added from the code block

    public Set<TargetingComponent> convertToComponents(Set<String> values) {
        Set<TargetingComponent> components = new HashSet<>();
        for (String value : values) {
            components.add(new TargetingComponent(this, value));
        }
        return components;
    }

    public List<TargetingComponent> convertToComponentList(Set<String> values) {
        List<TargetingComponent> components = new ArrayList<>();
        for (String value : values) {
            components.add(new TargetingComponent(this, value));
        }
        return components;
    }
} 