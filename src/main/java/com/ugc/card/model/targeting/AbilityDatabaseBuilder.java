package com.ugc.card.model.targeting;

import java.util.*;

public class AbilityDatabaseBuilder {
    private final TargetingSystem targetingSystem;
    private final AbilityDatabase abilityDatabase;
    
    public AbilityDatabaseBuilder(TargetingSystem targetingSystem) {
        this.targetingSystem = targetingSystem;
        this.abilityDatabase = new AbilityDatabase("abilities.csv", targetingSystem, "default");
    }
    
    public void buildFromComponents() {
        Set<TargetingComponent> components = targetingSystem.getComponents();
        Set<TargetingChain> chains = new HashSet<>();
        
        for (TargetingComponent component : components) {
            Set<TargetingChain> componentChains = generateChainsForComponent(component);
            chains.addAll(componentChains);
        }
        
        abilityDatabase.addChains(chains);
    }
    
    private Set<TargetingChain> generateChainsForComponent(TargetingComponent component) {
        Set<TargetingChain> chains = new HashSet<>();
        Set<TargetingComponent> chainComponents = new HashSet<>();
        chainComponents.add(component);
        chains.add(new TargetingChain(chainComponents));

        // Add combinations with other components
        for (TargetingComponent otherComponent : targetingSystem.getComponents()) {
            if (!otherComponent.equals(component)) {
                Set<TargetingComponent> combination = new HashSet<>(chainComponents);
                combination.add(otherComponent);
                chains.add(new TargetingChain(combination));
            }
        }

        return chains;
    }
    
    public void saveToFile(String filePath) {
        abilityDatabase.saveToFile(filePath);
    }
    
    public void loadFromFile(String filePath) {
        abilityDatabase.loadFromFile(filePath);
    }
    
    public Set<TargetingChain> getChains() {
        return abilityDatabase.getChains();
    }
} 