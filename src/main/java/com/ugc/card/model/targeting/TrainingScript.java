package com.ugc.card.model.targeting;

import java.util.*;

public class TrainingScript {
    public static void main(String[] args) {
        // Initialize the targeting system with required parameters
        Set<TargetingComponent> components = new HashSet<>();
        Map<String, TargetingComponent> componentMap = new HashMap<>();
        Map<ComponentCategory, Set<TargetingComponent>> componentsByCategory = new HashMap<>();
        Map<ComponentCategory, Set<String>> validValues = new HashMap<>();
        Map<String, GameAbilityParser> gameParsers = new HashMap<>();
        TargetingTensor model = new TargetingTensor(new int[]{100});
        Map<String, Set<ComponentCategory>> gameCategories = new HashMap<>();
        Map<String, Map<ComponentCategory, Set<String>>> gameComponents = new HashMap<>();
        Map<String, Set<ValidationRule>> gameRules = new HashMap<>();
        Map<String, Set<AbilityType>> gameAbilityTypes = new HashMap<>();
        Map<String, Set<TargetingPattern>> gamePatterns = new HashMap<>();
        Set<ValidationRule> validationRules = new HashSet<>();
        Set<TargetingPattern> targetingPatterns = new HashSet<>();

        TargetingSystem system = new TargetingSystem(
            components,
            componentMap,
            componentsByCategory,
            validValues,
            gameParsers,
            model,
            gameCategories,
            gameComponents,
            gameRules,
            gameAbilityTypes,
            gamePatterns,
            validationRules,
            targetingPatterns
        );
        
        // Choose a game to train for (e.g., "magic", "yugioh", "pokemon", etc.)
        String game = "magic";
        
        // Create training data generator
        TrainingDataGenerator generator = new TrainingDataGenerator(system, game);
        
        // Set up training parameters
        float learningRate = 0.001f;
        int epochs = 100;
        String modelPath = "data/models/targeting_model";
        
        // Create trainer
        TargetingTrainer trainer = new TargetingTrainer(game, system, learningRate, epochs, modelPath);
        
        // Generate training data (1000 examples)
        int numExamples = 1000;
        System.out.println("Generating " + numExamples + " training examples...");
        Set<TargetingChain> trainingData = generator.generateTrainingData(numExamples);
        
        // Train the model
        System.out.println("Starting training...");
        trainer.train(trainingData);
        
        // Save the trained model
        System.out.println("Training complete. Saving model...");
        trainer.saveModel();
        
        // Generate some example outputs to verify training
        System.out.println("\nGenerating example outputs:");
        List<TargetingChain> generated = system.generateChains(5);
        for (TargetingChain chain : generated) {
            System.out.println(system.generateAbilityText(game, chain));
        }
    }
} 