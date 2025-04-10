package com.ugc.card.model;

import java.util.Map;
import java.util.HashMap;

public class AbilityGenerator {
    private static final Map<EffectType, String> EFFECT_TEMPLATES = new HashMap<>();
    private static final Map<ConditionType, String> CONDITION_TEMPLATES = new HashMap<>();
    
    static {
        // Effect Templates
        EFFECT_TEMPLATES.put(EffectType.DAMAGE, "Deal {amount} damage to {target}");
        EFFECT_TEMPLATES.put(EffectType.HEAL, "Gain {amount} life");
        EFFECT_TEMPLATES.put(EffectType.MOVE, "Move {target} to {destination}");
        EFFECT_TEMPLATES.put(EffectType.DESTROY, "Destroy {target}");
        EFFECT_TEMPLATES.put(EffectType.MODIFY, "{target} gets {modification}");
        EFFECT_TEMPLATES.put(EffectType.DRAW, "Draw {amount} card(s)");
        EFFECT_TEMPLATES.put(EffectType.SEARCH, "Search your library for {target}");
        EFFECT_TEMPLATES.put(EffectType.COUNTER, "Counter {target}");
        
        // Condition Templates
        CONDITION_TEMPLATES.put(ConditionType.CONTROL, "if you control {target}");
        CONDITION_TEMPLATES.put(ConditionType.POWER, "if {target}'s power is {value}");
        CONDITION_TEMPLATES.put(ConditionType.TOUGHNESS, "if {target}'s toughness is {value}");
        CONDITION_TEMPLATES.put(ConditionType.CARD_TYPE, "if {target} is a {type}");
        CONDITION_TEMPLATES.put(ConditionType.ZONE, "if {target} is in {zone}");
        CONDITION_TEMPLATES.put(ConditionType.TRIGGER, "when {event}");
    }
    
    public static String generateAbility(TargetStructure structure) {
        StringBuilder sb = new StringBuilder();
        generateAbilityRecursive(structure, sb);
        return sb.toString().trim();
    }
    
    private static void generateAbilityRecursive(TargetStructure structure, StringBuilder sb) {
        if (structure == null) return;
        
        // Handle conditions
        if (structure.conditions != null && !structure.conditions.isEmpty()) {
            for (Condition condition : structure.conditions) {
                String template = CONDITION_TEMPLATES.get(condition.type);
                if (template != null) {
                    sb.append(applyTemplate(template, condition.parameters)).append(", ");
                }
            }
        }
        
        // Handle effect
        if (structure.effect != null) {
            String template = EFFECT_TEMPLATES.get(structure.effect.type);
            if (template != null) {
                sb.append(applyTemplate(template, structure.effect.parameters));
            }
        }
        
        // Handle chaining
        if (structure.next != null) {
            sb.append(", then ");
            generateAbilityRecursive(structure.next, sb);
        }
        
        if (structure.alternative != null) {
            sb.append(" or ");
            generateAbilityRecursive(structure.alternative, sb);
        }
    }
    
    private static String applyTemplate(String template, Map<String, Object> parameters) {
        String result = template;
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String key = "{" + entry.getKey() + "}";
                String value = entry.getValue().toString();
                result = result.replace(key, value);
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
        // Test cases
        TargetStructure damage = new TargetStructure();
        damage.targetType = TargetType.CREATURE;
        damage.effect = new Effect();
        damage.effect.type = EffectType.DAMAGE;
        damage.effect.parameters = Map.of("amount", 3);
        
        TargetStructure draw = new TargetStructure();
        draw.effect = new Effect();
        draw.effect.type = EffectType.DRAW;
        draw.effect.parameters = Map.of("amount", 2);
        
        TargetStructure destroy = new TargetStructure();
        destroy.targetType = TargetType.CREATURE;
        destroy.effect = new Effect();
        destroy.effect.type = EffectType.DESTROY;
        destroy.next = draw;
        
        System.out.println("Simple damage: " + generateAbility(damage));
        System.out.println("Destroy and draw: " + generateAbility(destroy));
    }
} 