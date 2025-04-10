package com.ugc.card.model.targeting;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TCGPlayerImporter {
    private final TargetingSystem targetingSystem;
    private final Map<String, GameAbilityParser> parsers;
    
    public TCGPlayerImporter(TargetingSystem targetingSystem) {
        this.targetingSystem = targetingSystem;
        this.parsers = new HashMap<>();
        initializeParsers();
    }
    
    private void initializeParsers() {
        parsers.put("snap", (GameAbilityParser) new SnapAbilityParser());
        parsers.put("starwars", (GameAbilityParser) new StarWarsAbilityParser());
        parsers.put("pokemon", (GameAbilityParser) new PokemonAbilityParser());
    }
    
    public Set<TargetingChain> importFromFile(String filePath) throws IOException {
        Set<TargetingChain> chains = new HashSet<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String game = parts[0].trim();
                    String ability = parts[1].trim();
                    GameAbilityParser parser = parsers.get(game.toLowerCase());
                    if (parser != null) {
                        TargetingChain chain = parser.parseAbility(ability);
                        if (chain != null) {
                            chains.add(chain);
                        }
                    }
                }
            }
        }
        
        return chains;
    }
    
    public void exportToFile(Set<TargetingChain> chains, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (TargetingChain chain : chains) {
                writer.write(chain.toString());
                writer.newLine();
            }
        }
    }

    public List<TargetingChain> importAbilities(List<String> lines) {
        List<TargetingChain> chains = new ArrayList<>();
        
        for (String line : lines) {
            try {
                TargetingChain chain = importAbility(line);
                if (chain != null) {
                    chains.add(chain);
                }
            } catch (Exception e) {
                System.err.println("Failed to parse ability: " + line);
                e.printStackTrace();
            }
        }
        
        return chains;
    }

    public Set<TargetingChain> importAbilitiesAsSet(List<String> lines) {
        return new HashSet<>(importAbilities(lines));
    }

    public TargetingChain importAbility(String line) {
        try {
            List<TargetingChain> chains = targetingSystem.parseAbility(line);
            return chains.isEmpty() ? null : chains.get(0);
        } catch (Exception e) {
            System.err.println("Failed to parse ability: " + line);
            e.printStackTrace();
            return null;
        }
    }
} 