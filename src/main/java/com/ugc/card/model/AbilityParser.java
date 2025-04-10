package com.ugc.card.model;

import java.util.*;
import java.util.regex.*;

public class AbilityParser {
    private static final Map<String, TargetType> TARGET_TYPE_MAP = Map.of(
        "creature", TargetType.CREATURE,
        "player", TargetType.PLAYER,
        "card", TargetType.CARD,
        "location", TargetType.LOCATION,
        "spell", TargetType.STACK,
        "hand", TargetType.HAND,
        "library", TargetType.LIBRARY,
        "graveyard", TargetType.GRAVEYARD,
        "battlefield", TargetType.BATTLEFIELD
    );

    private static final Map<String, EffectType> EFFECT_TYPE_MAP = Map.of(
        "deal.*damage", EffectType.DAMAGE,
        "gain.*life", EffectType.HEAL,
        "move", EffectType.MOVE,
        "destroy", EffectType.DESTROY,
        "modify", EffectType.MODIFY,
        "draw", EffectType.DRAW,
        "search", EffectType.SEARCH,
        "counter", EffectType.COUNTER
    );

    private static final Map<String, ConditionType> CONDITION_TYPE_MAP = Map.of(
        "control", ConditionType.CONTROL,
        "power", ConditionType.POWER,
        "toughness", ConditionType.TOUGHNESS,
        "type", ConditionType.CARD_TYPE,
        "zone", ConditionType.ZONE,
        "when", ConditionType.TRIGGER
    );

    public TargetStructure parseAbility(String abilityText) {
        // Normalize the text
        String normalizedText = abilityText.toLowerCase().trim();
        
        // Split into clauses
        String[] clauses = normalizedText.split("(,|then|and)");
        
        TargetStructure root = null;
        TargetStructure current = null;
        
        for (String clause : clauses) {
            TargetStructure structure = parseClause(clause.trim());
            
            if (root == null) {
                root = structure;
                current = structure;
            } else {
                current.next = structure;
                current = structure;
            }
        }
        
        return root;
    }

    private TargetStructure parseClause(String clause) {
        TargetStructure structure = new TargetStructure();
        
        // Parse target
        Matcher targetMatcher = Pattern.compile("target (\\w+)").matcher(clause);
        if (targetMatcher.find()) {
            String targetType = targetMatcher.group(1);
            structure.targetType = TARGET_TYPE_MAP.getOrDefault(targetType, TargetType.CARD);
        }
        
        // Parse effect
        for (Map.Entry<String, EffectType> entry : EFFECT_TYPE_MAP.entrySet()) {
            if (Pattern.compile(entry.getKey()).matcher(clause).find()) {
                structure.effect = new Effect();
                structure.effect.type = entry.getValue();
                structure.effect.parameters = parseEffectParameters(clause, entry.getValue());
                break;
            }
        }
        
        // Parse conditions
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, ConditionType> entry : CONDITION_TYPE_MAP.entrySet()) {
            if (Pattern.compile(entry.getKey()).matcher(clause).find()) {
                Condition condition = new Condition();
                condition.type = entry.getValue();
                condition.parameters = parseConditionParameters(clause, entry.getValue());
                conditions.add(condition);
            }
        }
        if (!conditions.isEmpty()) {
            structure.conditions = conditions;
        }
        
        return structure;
    }

    private Map<String, Object> parseEffectParameters(String clause, EffectType effectType) {
        Map<String, Object> parameters = new HashMap<>();
        
        switch (effectType) {
            case DAMAGE:
                Matcher damageMatcher = Pattern.compile("deal (\\d+) damage").matcher(clause);
                if (damageMatcher.find()) {
                    parameters.put("amount", Integer.parseInt(damageMatcher.group(1)));
                }
                break;
            case HEAL:
                Matcher healMatcher = Pattern.compile("gain (\\d+) life").matcher(clause);
                if (healMatcher.find()) {
                    parameters.put("amount", Integer.parseInt(healMatcher.group(1)));
                }
                break;
            case DRAW:
                Matcher drawMatcher = Pattern.compile("draw (\\d+) card").matcher(clause);
                if (drawMatcher.find()) {
                    parameters.put("amount", Integer.parseInt(drawMatcher.group(1)));
                }
                break;
        }
        
        return parameters;
    }

    private Map<String, Object> parseConditionParameters(String clause, ConditionType conditionType) {
        Map<String, Object> parameters = new HashMap<>();
        
        switch (conditionType) {
            case TRIGGER:
                Matcher triggerMatcher = Pattern.compile("when (.*)").matcher(clause);
                if (triggerMatcher.find()) {
                    parameters.put("triggerEvent", triggerMatcher.group(1));
                }
                break;
            case CONTROL:
                Matcher controlMatcher = Pattern.compile("control (\\d+)").matcher(clause);
                if (controlMatcher.find()) {
                    parameters.put("count", Integer.parseInt(controlMatcher.group(1)));
                }
                break;
        }
        
        return parameters;
    }

    public static void main(String[] args) {
        AbilityParser parser = new AbilityParser();
        
        // Test cases
        String[] testAbilities = {
            "Deal 3 damage to target creature",
            "Target creature gets +3/+3 until end of turn",
            "When this creature enters the battlefield, draw a card",
            "Search your library for a creature card, reveal it, put it into your hand, then shuffle your library",
            "Counter target spell",
            "Destroy target creature, then if that creature was destroyed this way, draw a card"
        };
        
        for (String ability : testAbilities) {
            System.out.println("Parsing: " + ability);
            TargetStructure structure = parser.parseAbility(ability);
            System.out.println("Result: " + structure);
            System.out.println();
        }
    }
} 