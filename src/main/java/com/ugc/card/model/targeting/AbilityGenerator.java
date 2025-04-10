package com.ugc.card.model.targeting;

import java.util.*;

public class AbilityGenerator {
    private final Map<String, String> effectTemplates;
    private final Map<String, String> triggerTemplates;
    private final Map<String, String> conditionTemplates;
    private final TargetingSystem targetingSystem;
    private final Random random;

    public AbilityGenerator(TargetingSystem targetingSystem) {
        this.targetingSystem = targetingSystem;
        this.random = new Random();
        effectTemplates = new HashMap<>();
        triggerTemplates = new HashMap<>();
        conditionTemplates = new HashMap<>();

        initializeTemplates();
    }

    private void initializeTemplates() {
        // Effect templates
        effectTemplates.put("deal", "deal {VALUE} damage to {DIRECTION} {STATE} {TYPE}");
        effectTemplates.put("draw", "{VALUE} card{PLURAL}");
        effectTemplates.put("restore", "restore {VALUE} Health to {DIRECTION} {STATE} {TYPE}");
        effectTemplates.put("gain", "gain {VALUE} {TYPE}");
        effectTemplates.put("summon", "summon {VALUE} {TYPE}");
        effectTemplates.put("discover", "discover {VALUE} {TYPE}");

        // Trigger templates
        triggerTemplates.put("battlecry", "Battlecry: {EFFECT}");
        triggerTemplates.put("deathrattle", "Deathrattle: {EFFECT}");
        triggerTemplates.put("at the end of", "At the end of {EFFECT}");
        triggerTemplates.put("at the start of", "At the start of {EFFECT}");

        // Condition templates
        conditionTemplates.put("if", "if {CONDITION}, {EFFECT}");
        conditionTemplates.put("unless", "unless {CONDITION}, {EFFECT}");
    }

    public String generateText(TargetingChain chain) {
        if (chain == null || chain.getComponents().isEmpty()) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        // Handle trigger if present
        if (chain.hasComponentOfCategory(ComponentCategory.TRIGGER)) {
            text.append(generateTriggerText(chain));
        }

        // Handle condition if present
        if (chain.hasComponentOfCategory(ComponentCategory.CONDITION)) {
            if (text.length() > 0) {
                text.append(" ");
            }
            text.append(generateConditionText(chain));
        }

        // Handle effects
        if (chain.hasComponentOfCategory(ComponentCategory.EFFECT)) {
            if (text.length() > 0 && !text.toString().endsWith(": ")) {
                text.append(" ");
            }
            text.append(generateEffectText(chain));
        }

        // Capitalize first letter
        if (text.length() > 0) {
            text.setCharAt(0, Character.toUpperCase(text.charAt(0)));
        }

        return text.toString();
    }

    private String generateTriggerText(TargetingChain chain) {
        String trigger = chain.getFirstComponentByCategory(ComponentCategory.TRIGGER).getText();
        String template = triggerTemplates.get(trigger.toLowerCase());
        
        if (template == null) {
            return trigger;
        }
        
        return template;
    }

    private String generateConditionText(TargetingChain chain) {
        String condition = chain.getFirstComponentByCategory(ComponentCategory.CONDITION).getText();
        String template = conditionTemplates.get(condition.toLowerCase());
        
        if (template == null) {
            return condition;
        }
        
        return template.replace("{CONDITION}", 
            condition.substring(condition.indexOf(" ") + 1));
    }

    private String generateEffectText(TargetingChain chain) {
        Set<TargetingComponent> effects = chain.getComponentsByCategory(ComponentCategory.EFFECT);
        if (effects.isEmpty()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        int i = 0;
        for (TargetingComponent effect : effects) {
            if (i > 0) {
                text.append(" and ");
            }

            String effectText = effect.getText();
            String template = effectTemplates.get(effectText.toLowerCase());
            
            if (template == null) {
                text.append(generateBasicEffectText(chain, effectText));
                i++;
                continue;
            }

            String effectTemplate = template;

            // Replace value if present
            if (chain.hasComponentOfCategory(ComponentCategory.VALUE)) {
                String value = chain.getFirstComponentByCategory(ComponentCategory.VALUE).getText();
                effectTemplate = effectTemplate.replace("{VALUE}", value);
                effectTemplate = effectTemplate.replace("{PLURAL}", value.equals("1") ? "" : "s");
            }

            // Replace direction if present
            if (chain.hasComponentOfCategory(ComponentCategory.DIRECTION)) {
                String direction = chain.getFirstComponentByCategory(ComponentCategory.DIRECTION).getText();
                effectTemplate = effectTemplate.replace("{DIRECTION}", direction);
            }

            // Replace state if present
            if (chain.hasComponentOfCategory(ComponentCategory.STATE)) {
                String state = chain.getFirstComponentByCategory(ComponentCategory.STATE).getText();
                effectTemplate = effectTemplate.replace("{STATE}", state);
            }

            // Replace type if present
            if (chain.hasComponentOfCategory(ComponentCategory.TYPE)) {
                String type = chain.getFirstComponentByCategory(ComponentCategory.TYPE).getText();
                effectTemplate = effectTemplate.replace("{TYPE}", type);
            }

            // Clean up any unused placeholders
            effectTemplate = effectTemplate.replaceAll("\\{[A-Z]+\\}", "");
            effectTemplate = effectTemplate.replaceAll("\\s+", " ").trim();

            text.append(effectTemplate);
            i++;
        }

        return text.toString();
    }

    private String generateBasicEffectText(TargetingChain chain, String effect) {
        StringBuilder text = new StringBuilder(effect);

        if (chain.hasComponentOfCategory(ComponentCategory.VALUE)) {
            text.append(" ").append(chain.getFirstComponentByCategory(ComponentCategory.VALUE).getText());
        }

        if (chain.hasComponentOfCategory(ComponentCategory.TYPE)) {
            text.append(" ").append(chain.getFirstComponentByCategory(ComponentCategory.TYPE).getText());
        }

        return text.toString();
    }

    public void addEffectTemplate(String effect, String template) {
        effectTemplates.put(effect.toLowerCase(), template);
    }

    public void addTriggerTemplate(String trigger, String template) {
        triggerTemplates.put(trigger.toLowerCase(), template);
    }

    public void addConditionTemplate(String condition, String template) {
        conditionTemplates.put(condition.toLowerCase(), template);
    }

    public Set<String> getEffectTemplates() {
        return new HashSet<>(effectTemplates.keySet());
    }

    public Set<String> getTriggerTemplates() {
        return new HashSet<>(triggerTemplates.keySet());
    }

    public Set<String> getConditionTemplates() {
        return new HashSet<>(conditionTemplates.keySet());
    }

    public Set<TargetingChain> generateAbilities(int count) {
        Set<TargetingChain> abilities = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            TargetingChain chain = new TargetingChain();
            
            // Add base component
            Set<TargetingComponent> baseComponents = targetingSystem.getComponentsByCategory(ComponentCategory.BASE);
            if (!baseComponents.isEmpty()) {
                chain.addComponent(getRandomComponent(baseComponents));
            }
            
            // Add target component
            Set<TargetingComponent> targetComponents = targetingSystem.getComponentsByCategory(ComponentCategory.TARGET);
            if (!targetComponents.isEmpty()) {
                chain.addComponent(getRandomComponent(targetComponents));
            }
            
            // Add type component
            Set<TargetingComponent> typeComponents = targetingSystem.getComponentsByCategory(ComponentCategory.TYPE);
            if (!typeComponents.isEmpty()) {
                chain.addComponent(getRandomComponent(typeComponents));
            }
            
            // Add effect component
            Set<TargetingComponent> effectComponents = targetingSystem.getComponentsByCategory(ComponentCategory.EFFECT);
            if (!effectComponents.isEmpty()) {
                chain.addComponent(getRandomComponent(effectComponents));
            }
            
            if (chain.isValid()) {
                abilities.add(chain);
            }
        }
        
        return abilities;
    }
    
    private TargetingComponent getRandomComponent(Set<TargetingComponent> components) {
        int index = random.nextInt(components.size());
        int currentIndex = 0;
        for (TargetingComponent component : components) {
            if (currentIndex == index) {
                return component;
            }
            currentIndex++;
        }
        return null;
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
        Set<String> values = targetingSystem.getValidComponents(category);
        return convertToComponents(values, category);
    }

    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        Set<String> values = targetingSystem.getValidComponents(category);
        return convertToComponentList(values, category);
    }

    private Set<TargetingComponent> getRandomComponents(ComponentCategory category, int count) {
        Set<TargetingComponent> components = targetingSystem.getComponentsByCategory(category);
        if (components.size() <= count) {
            return components;
        }
        List<TargetingComponent> componentList = new ArrayList<>(components);
        Collections.shuffle(componentList);
        return new HashSet<>(componentList.subList(0, count));
    }
} 