package com.ugc.card.model.targeting;

import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetingChain {
    private Set<TargetingComponent> components;
    private String qualifier;
    private String targetType;
    private String modifier;
    private String action;
    private String value;
    private Set<Float> numericalValues;
    
    public TargetingChain(Set<TargetingComponent> components) {
        this.components = new HashSet<>(components);
    }
    
    public TargetingChain() {
        this.components = new HashSet<>();
    }
    
    public Set<TargetingComponent> getComponents() {
        return new HashSet<>(components);
    }
    
    public Set<Float> getNumericalValues() {
        if (numericalValues == null) {
            numericalValues = new HashSet<>();
        }
        return numericalValues;
    }
    
    public void addComponent(TargetingComponent component) {
        components.add(component);
    }
    
    public void addNumericalValue(float value) {
        getNumericalValues().add(value);
    }
    
    public boolean isValid() {
        // A valid chain must have at least a BASE component and a TYPE component
        boolean hasBase = components.stream()
            .anyMatch(c -> c.getCategory() == ComponentCategory.BASE);
        boolean hasType = components.stream()
            .anyMatch(c -> c.getCategory() == ComponentCategory.TYPE);
        return hasBase && hasType;
    }
    
    @Override
    public String toString() {
        return "TargetingChain{" +
               "components=" + components +
               '}';
    }
    
    public boolean hasComponent(ComponentCategory category) {
        return components.stream().anyMatch(c -> c.getCategory() == category);
    }

    public boolean hasComponent(ComponentCategory category, String value) {
        return components.stream().anyMatch(c -> c.getCategory() == category && c.getValue().equals(value));
    }

    public boolean hasComponentOfCategory(ComponentCategory category) {
        return components.stream().anyMatch(c -> c.getCategory() == category);
    }
    
    public Set<String> getComponentsByCategory(ComponentCategory category) {
        return components.stream()
            .filter(c -> c.getCategory() == category)
            .map(TargetingComponent::getText)
            .collect(Collectors.toSet());
    }
    
    public TargetingComponent getFirstComponentByCategory(ComponentCategory category) {
        return components.stream()
            .filter(c -> c.getCategory() == category)
            .findFirst()
            .orElse(null);
    }
    
    public int size() {
        return components.size();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetingChain that = (TargetingChain) o;
        return components.equals(that.components);
    }
    
    @Override
    public int hashCode() {
        return components.hashCode();
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getText() {
        return components.stream()
            .map(TargetingComponent::getText)
            .collect(Collectors.joining(" "));
    }
    
    public List<TargetingComponent> getComponentsAsList() {
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

    public Set<TargetingComponent> getComponentsByType(ComponentCategory category) {
        return components.stream()
            .filter(c -> c.getCategory() == category)
            .collect(Collectors.toSet());
    }

    public List<TargetingComponent> getComponentsByTypeAsList(ComponentCategory category) {
        return components.stream()
            .filter(c -> c.getCategory() == category)
            .collect(Collectors.toList());
    }
} 