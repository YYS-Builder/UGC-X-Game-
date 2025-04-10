package com.ugc.card.model.targeting;

import java.util.*;
import java.util.stream.Collectors;

public class TargetingPatternGenerator {
    private TargetingSystem system;
    private TargetingTensor tensor;
    private Random random;
    private Map<ComponentCategory, Set<TargetingComponent>> componentsByCategory;
    
    public TargetingPatternGenerator(TargetingSystem system, TargetingTensor tensor) {
        this.system = system;
        this.tensor = tensor;
        this.random = new Random();
        this.componentsByCategory = new EnumMap<>(ComponentCategory.class);
        initializeComponentCategories();
    }
    
    private void initializeComponentCategories() {
        for (ComponentCategory category : ComponentCategory.values()) {
            componentsByCategory.put(category, new HashSet<>());
        }
        
        // Add all components to their respective categories
        for (TargetingComponent component : system.getComponents()) {
            componentsByCategory.get(component.getCategory()).add(component);
        }
    }
    
    public TargetingChain generatePattern(int maxComponents) {
        TargetingChain chain = new TargetingChain();
        
        // Start with EFFECT as it determines valid combinations
        ComponentCategory firstCategory = ComponentCategory.EFFECT;
        
        // Get components for first category
        Set<TargetingComponent> effectComponents = componentsByCategory.get(firstCategory);
        if (effectComponents.isEmpty()) {
            return null;
        }
        
        // Add first component (effect)
        TargetingComponent effectComponent = effectComponents.iterator().next();
        chain.addComponent(effectComponent);
        String effect = effectComponent.getText().toLowerCase();
        
        // Determine required components based on effect
        boolean needsValue = effect.equals("damage") || effect.equals("heal") || 
                           effect.equals("draw") || effect.equals("mill");
        boolean canBeRandom = effect.equals("counter") || effect.equals("discard");
        boolean needsSpellType = effect.equals("counter");
        
        // Add appropriate TYPE component
        Set<TargetingComponent> typeComponents = componentsByCategory.get(ComponentCategory.TYPE);
        TargetingComponent typeComponent = null;
        
        if (needsSpellType) {
            // Filter for spell types
            Set<TargetingComponent> spellTypes = typeComponents.stream()
                .filter(c -> c.getText().equals("spell") || c.getText().equals("ability"))
                .collect(Collectors.toSet());
            if (!spellTypes.isEmpty()) {
                typeComponent = spellTypes.iterator().next();
            }
        } else {
            // Filter out inappropriate types
            Set<TargetingComponent> validTypes = typeComponents.stream()
                .filter(c -> !c.getText().equals("tapped") && 
                           !c.getText().equals("spell") && 
                           !c.getText().equals("ability"))
                .collect(Collectors.toSet());
            if (!validTypes.isEmpty()) {
                typeComponent = validTypes.iterator().next();
            }
        }
        
        if (typeComponent != null) {
            chain.addComponent(typeComponent);
        } else {
            return null;
        }
        
        // Add DIRECTION if appropriate
        if (random.nextBoolean()) {
            Set<TargetingComponent> directionComponents = componentsByCategory.get(ComponentCategory.DIRECTION);
            Set<TargetingComponent> validDirections = directionComponents.stream()
                .filter(c -> {
                    final String direction = c.getText().toLowerCase();
                    final String type = typeComponent.getText().toLowerCase();
                    final boolean isCreature = type.equals("creature");
                    if (direction.equals("random") && !isCreature) {
                        return false;
                    }
                    if (direction.equals("another")) {
                        return type.equals("creature") || 
                               type.equals("artifact") || 
                               type.equals("enchantment");
                    }
                    return true;
                })
                .collect(Collectors.toSet());
            
            if (!validDirections.isEmpty()) {
                chain.addComponent(validDirections.iterator().next());
            }
        }
        
        // Add VALUE if needed
        if (needsValue) {
            Set<TargetingComponent> valueComponents = componentsByCategory.get(ComponentCategory.VALUE);
            Set<TargetingComponent> validValues = valueComponents.stream()
                .filter(c -> {
                    String value = c.getText();
                    try {
                        int numValue = Integer.parseInt(value);
                        return numValue > 0 && numValue <= 15;
                    } catch (NumberFormatException e) {
                        return value.equalsIgnoreCase("X");
                    }
                })
                .collect(Collectors.toSet());
            
            if (!validValues.isEmpty()) {
                chain.addComponent(validValues.iterator().next());
            }
        }
        
        return chain.getComponents().size() >= 2 ? chain : null;
    }
    
    private ComponentCategory selectNextCategory(float[] predictions, Set<ComponentCategory> availableCategories) {
        // Convert predictions to probabilities for available categories
        Map<ComponentCategory, Double> categoryProbabilities = new HashMap<>();
        double total = 0.0;
        
        for (ComponentCategory category : availableCategories) {
            int categoryIndex = category.ordinal();
            double probability = predictions[categoryIndex];
            categoryProbabilities.put(category, probability);
            total += probability;
        }
        
        // Normalize probabilities
        if (total > 0) {
            for (ComponentCategory category : availableCategories) {
                categoryProbabilities.put(category, categoryProbabilities.get(category) / total);
            }
        }
        
        // Select category based on probabilities
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        
        for (ComponentCategory category : availableCategories) {
            cumulativeProbability += categoryProbabilities.get(category);
            if (randomValue <= cumulativeProbability) {
                return category;
            }
        }
        
        return null;
    }
    
    private TargetingTensor forward(TargetingTensor input) {
        float[] inputValues = input.getValues();
        float[] outputValues = new float[inputValues.length];
        
        for (int i = 0; i < inputValues.length; i++) {
            float sum = 0;
            for (int j = 0; j < inputValues.length; j++) {
                sum += inputValues[j] * tensor.getValue(new int[]{j, i});
            }
            outputValues[i] = sigmoid(sum);
        }
        
        TargetingTensor output = new TargetingTensor(new int[]{inputValues.length});
        output.setValues(outputValues);
        return output;
    }
    
    private float sigmoid(float x) {
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }
    
    public List<TargetingComponent> getTopComponents(Set<TargetingComponent> components, int count) {
        final Set<TargetingComponent> finalComponents = new HashSet<>(components);
        return components.stream()
            .sorted((c1, c2) -> {
                int count1 = getComponentCount(finalComponents, c1);
                int count2 = getComponentCount(finalComponents, c2);
                return Integer.compare(count2, count1);
            })
            .limit(count)
            .collect(Collectors.toList());
    }
    
    private static class Pair<F, S> {
        private final F first;
        private final S second;
        
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
        
        public F getFirst() { return first; }
        public S getSecond() { return second; }
    }
    
    public Set<TargetingPattern> generatePatterns(Set<TargetingChain> chains) {
        Set<TargetingPattern> patterns = new HashSet<>();
        
        for (TargetingChain chain : chains) {
            Set<ComponentCategory> requiredCategories = new HashSet<>();
            Set<ComponentCategory> optionalCategories = new HashSet<>();
            Map<ComponentCategory, Set<String>> validComponents = new HashMap<>();
            
            for (TargetingComponent component : chain.getComponents()) {
                requiredCategories.add(component.getCategory());
                validComponents.computeIfAbsent(component.getCategory(), k -> new HashSet<>())
                    .add(component.getValue());
            }
            
            patterns.add(new TargetingPattern(
                "Generated Pattern",
                requiredCategories,
                optionalCategories,
                validComponents
            ));
        }
        
        return patterns;
    }
    
    private int getComponentIndex(Set<TargetingComponent> components, TargetingComponent component) {
        List<TargetingComponent> componentList = new ArrayList<>(components);
        return componentList.indexOf(component);
    }

    private List<TargetingComponent> getComponentsAsList(Set<TargetingComponent> components) {
        return new ArrayList<>(components);
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
        Set<String> values = system.getValidComponents(category);
        return convertToComponents(values, category);
    }

    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        Set<String> values = system.getValidComponents(category);
        return convertToComponentList(values, category);
    }
} 