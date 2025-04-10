package com.ugc.card.model.targeting;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AbilityDatabase {
    private final String databasePath;
    private final TargetingSystem targetingSystem;
    private final String game;
    private final Map<String, Set<TargetingChain>> abilities;
    private final Map<ComponentCategory, Set<String>> validValues;
    private Set<TargetingChain> abilitiesSet;
    private Map<ComponentCategory, Set<TargetingChain>> categoryMap;
    
    public AbilityDatabase(String databasePath, TargetingSystem targetingSystem, String game) {
        this.databasePath = databasePath;
        this.targetingSystem = targetingSystem;
        this.game = game.toLowerCase();
        if (!targetingSystem.isGameSupported(game)) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        this.abilities = new HashMap<>();
        this.validValues = new HashMap<>();
        this.abilitiesSet = new HashSet<>();
        this.categoryMap = new HashMap<>();
        
        // Initialize valid values for each category
        for (ComponentCategory category : targetingSystem.getValidCategories(game)) {
            validValues.put(category, new HashSet<>());
        }
        
        loadDatabase();
    }
    
    public AbilityDatabase() {
        this.databasePath = "abilities.csv";
        this.targetingSystem = new TargetingSystem();
        this.game = "default";
        this.abilities = new HashMap<>();
        this.validValues = new HashMap<>();
        this.abilitiesSet = new HashSet<>();
        this.categoryMap = new HashMap<>();
    }
    
    private void loadDatabase() {
        File dbFile = new File(databasePath);
        if (!dbFile.exists()) {
            return;
        }
        
        try (CSVReader reader = new CSVReader(new FileReader(dbFile))) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                if (row.length >= 2) {
                    String abilityText = row[0];
                    String chainText = row[1];
                    
                    // Parse the targeting chain
                    Set<TargetingChain> chains = parseChainText(chainText);
                    abilities.computeIfAbsent(abilityText, k -> new HashSet<>()).addAll(chains);
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
    
    private Set<TargetingChain> parseChainText(String chainText) {
        Set<TargetingChain> chains = new HashSet<>();
        String[] chainParts = chainText.split("\\|");
        
        for (String part : chainParts) {
            TargetingChain chain = new TargetingChain();
            String[] components = part.split(",");
            
            for (String comp : components) {
                String[] parts = comp.split(":");
                if (parts.length == 2) {
                    String value = parts[0];
                    ComponentCategory category = ComponentCategory.valueOf(parts[1]);
                    chain.addComponent(new TargetingComponent(category, value));
                }
            }
            chains.add(chain);
        }
        return chains;
    }
    
    public void addAbility(String name, TargetingChain chain) {
        abilities.computeIfAbsent(name, k -> new HashSet<>()).add(chain);
        
        // Update valid values
        for (TargetingComponent component : chain.getComponents()) {
            validValues.computeIfAbsent(component.getCategory(), k -> new HashSet<>())
                      .add(component.getText());
        }
        
        // Update category map
        for (TargetingComponent component : chain.getComponents()) {
            categoryMap.computeIfAbsent(component.getCategory(), k -> new HashSet<>()).add(chain);
        }
        
        abilitiesSet.add(chain);
        saveDatabase();
    }
    
    public void removeAbility(String name) {
        abilities.remove(name);
        saveDatabase();
    }
    
    private void saveDatabase() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(databasePath))) {
            for (Map.Entry<String, Set<TargetingChain>> entry : abilities.entrySet()) {
                String abilityText = entry.getKey();
                String chainText = serializeChains(entry.getValue());
                writer.writeNext(new String[]{abilityText, chainText});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String serializeChains(Set<TargetingChain> chains) {
        StringBuilder sb = new StringBuilder();
        for (TargetingChain chain : chains) {
            if (sb.length() > 0) sb.append("|");
            int i = 0;
            for (TargetingComponent comp : chain.getComponents()) {
                if (i > 0) sb.append(",");
                sb.append(comp.getText()).append(":").append(comp.getCategory());
                i++;
            }
        }
        return sb.toString();
    }
    
    public Set<TargetingChain> getAbility(String name) {
        return abilities.getOrDefault(name, Collections.emptySet());
    }
    
    public Set<String> getValidValues(ComponentCategory category) {
        return validValues.getOrDefault(category, Collections.emptySet());
    }
    
    public Set<ComponentCategory> getValidCategories() {
        return validValues.keySet();
    }
    
    public Set<String> getAllAbilities() {
        return abilities.keySet();
    }
    
    public Set<TargetingChain> getChainsForAbility(String abilityText) {
        return abilities.getOrDefault(abilityText, Collections.emptySet());
    }
    
    public int size() {
        return abilities.size();
    }
    
    public void clear() {
        abilities.clear();
        saveDatabase();
    }
    
    public void importFromCSV(String csvFile) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                if (row.length > 0) {
                    String abilityText = row[0];
                    TargetingChain chain = targetingSystem.parseAbility(game, abilityText);
                    if (chain != null) {
                        addAbility(abilityText, chain);
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
    
    public void exportToCSV(String csvFile) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            for (String ability : getAllAbilities()) {
                writer.writeNext(new String[]{ability});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Set<TargetingChain> getAbilitiesByCategory(ComponentCategory category) {
        return categoryMap.getOrDefault(category, Collections.emptySet());
    }
    
    public Set<TargetingChain> searchAbilities(String query) {
        return abilitiesSet.stream()
            .filter(ability -> matchesQuery(ability, query))
            .collect(Collectors.toSet());
    }
    
    private boolean matchesQuery(TargetingChain ability, String query) {
        // Check if any component matches the query
        return ability.getComponents().stream()
            .anyMatch(comp -> comp.getText().toLowerCase().contains(query.toLowerCase()));
    }
    
    public Set<TargetingChain> getSimilarAbilities(TargetingChain ability) {
        return abilitiesSet.stream()
            .filter(other -> !other.equals(ability))
            .sorted((a, b) -> Double.compare(
                calculateSimilarity(b, ability),
                calculateSimilarity(a, ability)
            ))
            .limit(10)
            .collect(Collectors.toSet());
    }
    
    private double calculateSimilarity(TargetingChain a, TargetingChain b) {
        Set<ComponentCategory> categories = new HashSet<>();
        categories.addAll(a.getComponents().stream().map(TargetingComponent::getCategory).collect(Collectors.toSet()));
        categories.addAll(b.getComponents().stream().map(TargetingComponent::getCategory).collect(Collectors.toSet()));
        
        double similarity = 0.0;
        for (ComponentCategory category : categories) {
            Set<String> valuesA = a.getComponents().stream()
                .filter(c -> c.getCategory() == category)
                .map(TargetingComponent::getText)
                .collect(Collectors.toSet());
            
            Set<String> valuesB = b.getComponents().stream()
                .filter(c -> c.getCategory() == category)
                .map(TargetingComponent::getText)
                .collect(Collectors.toSet());
            
            similarity += calculateValueSimilarity(valuesA, valuesB);
        }
        
        return similarity / categories.size();
    }
    
    private double calculateValueSimilarity(Set<String> valuesA, Set<String> valuesB) {
        if (valuesA.isEmpty() && valuesB.isEmpty()) {
            return 1.0;
        }
        if (valuesA.isEmpty() || valuesB.isEmpty()) {
            return 0.0;
        }
        
        int matches = 0;
        for (String valueA : valuesA) {
            for (String valueB : valuesB) {
                if (valueA.equals(valueB)) {
                    matches++;
                }
            }
        }
        
        return (double) matches / Math.max(valuesA.size(), valuesB.size());
    }
    
    public void addChains(Set<TargetingChain> chains) {
        for (TargetingChain chain : chains) {
            abilitiesSet.add(chain);
            for (TargetingComponent component : chain.getComponents()) {
                categoryMap.computeIfAbsent(component.getCategory(), k -> new HashSet<>()).add(chain);
            }
        }
    }
    
    public void saveToFile(String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            for (TargetingChain chain : abilitiesSet) {
                writer.writeNext(new String[]{chain.getText()});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadFromFile(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                if (row.length > 0) {
                    String abilityText = row[0];
                    TargetingChain chain = targetingSystem.parseAbility(game, abilityText);
                    if (chain != null) {
                        addAbility(abilityText, chain);
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
    
    public Set<TargetingChain> getChains() {
        return new HashSet<>(abilitiesSet);
    }
} 