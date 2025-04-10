package com.ugc.card.model.targeting;

import java.util.*;

public class TargetingPattern {
    private final String name;
    private final Set<ComponentCategory> requiredCategories;
    private final Set<ComponentCategory> optionalCategories;
    private final Map<ComponentCategory, Set<String>> validComponents;

    public TargetingPattern(String name, 
                          Set<ComponentCategory> requiredCategories,
                          Set<ComponentCategory> optionalCategories,
                          Map<ComponentCategory, Set<String>> validComponents) {
        this.name = name;
        this.requiredCategories = new HashSet<>(requiredCategories);
        this.optionalCategories = new HashSet<>(optionalCategories);
        this.validComponents = new HashMap<>();
        
        // Deep copy the valid components map
        for (Map.Entry<ComponentCategory, Set<String>> entry : validComponents.entrySet()) {
            this.validComponents.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    public boolean matches(TargetingChain chain) {
        if (chain == null) {
            return false;
        }

        // Check required categories
        for (ComponentCategory category : requiredCategories) {
            if (!chain.hasComponentOfCategory(category)) {
                return false;
            }
        }

        // Check that all components in the chain are either required or optional
        for (TargetingComponent component : chain.getComponents()) {
            ComponentCategory category = component.getCategory();
            if (!requiredCategories.contains(category) && !optionalCategories.contains(category)) {
                return false;
            }
        }

        // Validate components against valid components
        for (TargetingComponent component : chain.getComponents()) {
            ComponentCategory category = component.getCategory();
            Set<String> validValues = validComponents.get(category);
            
            if (validValues != null && !validValues.contains(component.getText().toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    public String getName() {
        return name;
    }

    public Set<ComponentCategory> getRequiredCategories() {
        return new HashSet<>(requiredCategories);
    }

    public Set<ComponentCategory> getOptionalCategories() {
        return new HashSet<>(optionalCategories);
    }

    public Map<ComponentCategory, Set<String>> getValidComponents() {
        Map<ComponentCategory, Set<String>> copy = new HashMap<>();
        for (Map.Entry<ComponentCategory, Set<String>> entry : validComponents.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return copy;
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
        Set<String> values = getValidComponents().get(category);
        return convertToComponents(values, category);
    }

    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        Set<String> values = getValidComponents().get(category);
        return convertToComponentList(values, category);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetingPattern that = (TargetingPattern) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(requiredCategories, that.requiredCategories) &&
               Objects.equals(optionalCategories, that.optionalCategories) &&
               Objects.equals(validComponents, that.validComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, requiredCategories, optionalCategories, validComponents);
    }

    @Override
    public String toString() {
        return "TargetingPattern{" +
               "name='" + name + '\'' +
               ", requiredCategories=" + requiredCategories +
               ", optionalCategories=" + optionalCategories +
               ", validComponents=" + validComponents +
               '}';
    }
} 