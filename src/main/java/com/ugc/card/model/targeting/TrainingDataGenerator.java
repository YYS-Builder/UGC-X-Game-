package com.ugc.card.model.targeting;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TrainingDataGenerator {
    private final TargetingSystem system;
    private final Random random;
    private final String game;
    
    public TrainingDataGenerator(String game) {
        this.game = game;
        this.system = new TargetingSystem(
            new HashSet<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new TargetingTensor(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashMap<>(),
            new HashSet<>(),
            new HashSet<>()
        );
        this.random = new Random();
        if (!system.isGameSupported(game)) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
    }
    
    public void generateTrainingData(String inputFile, String outputFile) {
        try {
            // Read the CSV file
            List<String[]> rows = readCSV(inputFile);
            
            // Generate training examples
            List<String> trainingExamples = new ArrayList<>();
            
            // Add header
            trainingExamples.add("Ability Text,Targeting Pattern");
            
            // Process each row
            for (String[] row : rows) {
                if (row.length >= 2) {
                    String base = row[0];
                    String type = row[1];
                    
                    // Skip header row
                    if (base.equals("Base") || base.isEmpty()) continue;
                    
                    // Generate example abilities
                    List<String> examples = generateExamples(base, type, row);
                    trainingExamples.addAll(examples);
                }
            }
            
            // Write to output file
            writeCSV(outputFile, trainingExamples);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private List<String[]> readCSV(String filePath) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            return reader.readAll();
        }
    }
    
    private void writeCSV(String filePath, List<String> lines) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }
    
    private List<String> generateExamples(String base, String type, String[] row) {
        List<String> examples = new ArrayList<>();
        
        // Create targeting chain
        TargetingChain chain = new TargetingChain();
        
        // Add base component
        chain.addComponent(new TargetingComponent(ComponentCategory.BASE, base));
        
        // Add type component
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, type));
        
        // Add other components based on row data
        if (row.length > 2 && !row[2].isEmpty()) {
            chain.addComponent(new TargetingComponent(ComponentCategory.ON_CARD, row[2]));
        }
        
        if (row.length > 3 && !row[3].isEmpty()) {
            chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, row[3]));
        }
        
        if (row.length > 4 && !row[4].isEmpty()) {
            chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, row[4]));
        }
        
        if (row.length > 5 && !row[5].isEmpty()) {
            chain.addComponent(new TargetingComponent(ComponentCategory.RESULT, row[5]));
        }
        
        // Generate natural language
        String abilityText = system.generateAbilityText(game, chain);
        
        // Add to examples
        examples.add(abilityText + "," + chain.toString());
        
        return examples;
    }
    
    public static void main(String[] args) {
        TargetingSystem system = new TargetingSystem();
        TrainingDataGenerator generator = new TrainingDataGenerator("snap");
        
        // Generate training data
        generator.generateTrainingData(
            "UGC Card Model/Out of Peters head - Build Table.csv",
            "UGC Card Model/data/training/training_data.csv"
        );
        
        // Train the model
        TargetingTrainer trainer = new TargetingTrainer("snap", system, 0.001f, 100, "UGC Card Model/data/models/targeting_model");
        trainer.train(new HashSet<>(generator.readTrainingData("UGC Card Model/data/training/training_data.csv")));
    }
    
    private List<TargetingChain> readTrainingData(String csvFile) {
        List<TargetingChain> chains = new ArrayList<>();
        try {
            List<String[]> rows = readCSV(csvFile);
            // Skip header
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length >= 1) {
                    String abilityText = row[0];
                    TargetingChain chain = system.parseAbility(game, abilityText);
                    if (chain != null) {
                        chains.add(chain);
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return chains;
    }

    public Set<TargetingChain> generateTrainingData(int count) {
        Set<TargetingChain> chains = new HashSet<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            TargetingChain chain = new TargetingChain();
            int componentCount = random.nextInt(3) + 2; // 2-4 components
            
            for (int j = 0; j < componentCount; j++) {
                ComponentCategory category = getRandomCategory(random);
                String value = getRandomValue(category, random);
                chain.addComponent(new TargetingComponent(category, value));
            }
            
            chains.add(chain);
        }
        
        return chains;
    }
    
    public List<TargetingChain> generateTrainingDataList(int count) {
        List<TargetingChain> chains = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            TargetingChain chain = new TargetingChain();
            int componentCount = random.nextInt(3) + 2; // 2-4 components
            
            for (int j = 0; j < componentCount; j++) {
                ComponentCategory category = getRandomCategory(random);
                String value = getRandomValue(category, random);
                chain.addComponent(new TargetingComponent(category, value));
            }
            
            chains.add(chain);
        }
        
        return chains;
    }
    
    private ComponentCategory getRandomCategory(Random random) {
        ComponentCategory[] categories = ComponentCategory.values();
        return categories[random.nextInt(categories.length)];
    }
    
    private String getRandomValue(ComponentCategory category, Random random) {
        Set<String> values = system.getValidComponents(game, category);
        if (values.isEmpty()) {
            return "default";
        }
        List<String> valueList = new ArrayList<>(values);
        return valueList.get(random.nextInt(valueList.size()));
    }

    public void train(TargetingTrainer trainer, int count) {
        List<TargetingChain> trainingData = generateTrainingDataList(count);
        trainer.train(new HashSet<>(trainingData));
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
        Set<String> values = system.getValidComponents(game, category);
        return convertToComponents(values, category);
    }

    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        Set<String> values = system.getValidComponents(game, category);
        return convertToComponentList(values, category);
    }
} 