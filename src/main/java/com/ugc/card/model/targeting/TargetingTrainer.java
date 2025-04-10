package com.ugc.card.model.targeting;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Data
public class TargetingTrainer {
    private TargetingSystem system;
    private TargetingTensor tensor;
    private float learningRate;
    private int epochs;
    private String modelPath;
    private List<TrainingMetrics> trainingHistory;
    private Map<TargetingChain, Double> patternDifficulties;
    private double initialLearningRate;
    private double bestValidationLoss = Double.MAX_VALUE;
    private int bestEpoch = 0;
    private TargetingTensor bestModel = null;
    private Map<String, Map<String, Integer>> transitionCounts = new HashMap<>();
    private Map<String, Integer> totalTransitions = new HashMap<>();
    private final String game;
    
    public TargetingTrainer(String game, TargetingSystem system, float learningRate, int epochs, String modelPath) {
        this.game = game;
        this.system = system;
        this.initialLearningRate = learningRate;
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.modelPath = modelPath;
        this.tensor = new TargetingTensor(new int[]{system.getVectorSize(), system.getVectorSize()});
        this.trainingHistory = new ArrayList<>();
        this.patternDifficulties = new HashMap<>();
        
        // Try to load previous best model
        loadBestModel();
    }
    
    private void loadBestModel() {
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile))) {
                TargetingTensor loadedTensor = (TargetingTensor) ois.readObject();
                // Initialize current tensor with loaded weights
                this.tensor = loadedTensor;
                System.out.println("Loaded previous best model successfully");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("No previous model found or error loading it. Starting fresh.");
            }
        }
    }
    
    private void saveBestModel() {
        if (bestModel != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
                oos.writeObject(bestModel);
                System.out.println("Saved new best model");
            } catch (IOException e) {
                System.err.println("Error saving best model: " + e.getMessage());
            }
        }
    }
    
    private void updateBestModel(double validationLoss, int epoch) {
        if (validationLoss < bestValidationLoss) {
            bestValidationLoss = validationLoss;
            bestEpoch = epoch;
            // Create a deep copy of the current tensor
            bestModel = new TargetingTensor(tensor.getShape());
            float[] values = tensor.getValues();
            bestModel.setValues(Arrays.copyOf(values, values.length));
        }
    }
    
    private double calculatePatternDifficulty(TargetingChain chain) {
        List<TargetingComponent> components = chain.getComponents();
        double difficulty = 0.0;
        
        // Base difficulty from length
        difficulty += components.size() * 0.5;
        
        // Category variety bonus
        Set<ComponentCategory> uniqueCategories = components.stream()
            .map(TargetingComponent::getCategory)
            .collect(Collectors.toSet());
        difficulty += uniqueCategories.size() * 0.75;
        
        // Special component bonus
        for (TargetingComponent component : components) {
            switch (component.getText()) {
                case "up to":
                case "another":
                case "X":
                    difficulty += 1.0;
                    break;
                case "random":
                case "each":
                    difficulty += 0.75;
                    break;
            }
        }
        
        return difficulty;
    }
    
    private List<TargetingChain> getCurriculumBatch(List<TargetingChain> allData, int epoch) {
        // Calculate difficulties for all patterns if not done
        if (patternDifficulties.isEmpty()) {
            for (TargetingChain chain : allData) {
                patternDifficulties.put(chain, calculatePatternDifficulty(chain));
            }
        }
        
        // Calculate curriculum progress (0.0 to 1.0)
        double progress = Math.min(1.0, (double)epoch / (epochs * 0.7));
        
        // Sort patterns by difficulty
        List<TargetingChain> sortedPatterns = new ArrayList<>(allData);
        sortedPatterns.sort((a, b) -> 
            Double.compare(patternDifficulties.get(a), patternDifficulties.get(b)));
        
        // Calculate how many patterns to include based on progress
        int numPatterns = (int)(sortedPatterns.size() * (0.3 + 0.7 * progress));
        numPatterns = Math.max(sortedPatterns.size() / 3, 
                     Math.min(sortedPatterns.size(), numPatterns));
        
        // Select patterns up to the difficulty threshold
        return sortedPatterns.subList(0, numPatterns);
    }
    
    private void updateLearningRate(int epoch, double validationLoss) {
        // More gradual learning rate schedule
        double progress = (double)epoch / epochs;
        if (progress < 0.3) {
            // Initial high learning rate phase
            learningRate = (float)(initialLearningRate * 1.2);
        } else if (progress < 0.6) {
            // Middle phase with moderate learning rate
            learningRate = (float)initialLearningRate;
        } else {
            // Final phase with very gradual reduction
            learningRate = (float)(initialLearningRate * Math.pow(0.98, (epoch - epochs * 0.6)));
        }
    }
    
    public void train(Set<TargetingChain> trainingData) {
        for (TargetingChain chain : trainingData) {
            List<TargetingComponent> components = new ArrayList<>(chain.getComponents());
            for (int i = 0; i < components.size() - 1; i++) {
                TargetingComponent current = components.get(i);
                TargetingComponent next = components.get(i + 1);
                updateTransitionProbability(current, next);
            }
        }
    }
    
    private void updateTransitionProbability(TargetingComponent from, TargetingComponent to) {
        String fromKey = from.getCategory() + ":" + from.getValue();
        String toKey = to.getCategory() + ":" + to.getValue();
        
        transitionCounts.computeIfAbsent(fromKey, k -> new HashMap<>())
                       .merge(toKey, 1, Integer::sum);
        totalTransitions.merge(fromKey, 1, Integer::sum);
    }
    
    private void printComponentDistribution(List<TargetingChain> chains) {
        Map<ComponentCategory, Integer> distribution = new EnumMap<>(ComponentCategory.class);
        for (TargetingChain chain : chains) {
            for (TargetingComponent component : chain.getComponents()) {
                distribution.merge(component.getCategory(), 1, Integer::sum);
            }
        }
        
        distribution.forEach((category, count) -> 
            System.out.printf("%s: %d components%n", category, count));
    }
    
    private void updateCategoryLosses(Map<ComponentCategory, Double> categoryLosses, 
                                    TargetingChain chain, double loss) {
        for (TargetingComponent component : chain.getComponents()) {
            categoryLosses.merge(
                component.getCategory(),
                loss,
                Double::sum
            );
        }
    }
    
    private Map<ComponentCategory, Double> calculateAverageCategoryLosses(
            Map<ComponentCategory, Double> totalLosses, int dataSize) {
        return totalLosses.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue() / dataSize,
                (a, b) -> a,
                () -> new EnumMap<>(ComponentCategory.class)
            ));
    }
    
    private double calculatePatternComplexity(List<TargetingChain> chains) {
        return chains.stream()
            .mapToDouble(chain -> {
                int componentCount = chain.getComponents().size();
                int categoryCount = (int)chain.getComponents().stream()
                    .map(TargetingComponent::getCategory)
                    .distinct()
                    .count();
                return (componentCount * categoryCount) / 2.0;
            })
            .average()
            .orElse(0.0);
    }
    
    private void printPatternComplexityMetrics(List<TargetingChain> chains) {
        DoubleSummaryStatistics complexityStats = chains.stream()
            .mapToDouble(chain -> chain.getComponents().size())
            .summaryStatistics();
            
        System.out.printf("Average Components per Chain: %.2f%n", complexityStats.getAverage());
        System.out.printf("Min Components: %d%n", (int)complexityStats.getMin());
        System.out.printf("Max Components: %d%n", (int)complexityStats.getMax());
        
        double avgCategories = chains.stream()
            .mapToDouble(chain -> chain.getComponents().stream()
                .map(TargetingComponent::getCategory)
                .distinct()
                .count())
            .average()
            .orElse(0.0);
            
        System.out.printf("Average Categories per Chain: %.2f%n", avgCategories);
    }
    
    private void saveTrainingHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(modelPath + "_history.bin"))) {
            oos.writeObject(trainingHistory);
        } catch (IOException e) {
            System.err.println("Error saving training history: " + e.getMessage());
        }
    }
    
    private void printFinalMetrics() {
        if (trainingHistory.isEmpty()) return;
        
        TrainingMetrics finalMetrics = trainingHistory.get(trainingHistory.size() - 1);
        TrainingMetrics bestMetrics = trainingHistory.stream()
            .min(Comparator.comparingDouble(m -> m.validationLoss))
            .get();
            
        System.out.println("\nFinal Training Metrics:");
        System.out.printf("Best Validation Loss: %.4f (Epoch %d)%n",
            bestMetrics.validationLoss, bestMetrics.epoch);
        System.out.printf("Final Training Loss: %.4f%n", finalMetrics.trainingLoss);
        System.out.printf("Final Validation Loss: %.4f%n", finalMetrics.validationLoss);
        System.out.printf("Final Pattern Complexity: %.2f%n", finalMetrics.patternComplexity);
        System.out.println("\nCategory-wise Final Losses:");
        finalMetrics.categoryLosses.forEach((category, loss) ->
            System.out.printf("%s: %.4f%n", category, loss));
    }
    
    private TargetingTensor forward(TargetingChain chain) {
        TargetingTensor input = system.chainToTensor(chain);
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
    
    private void backward(TargetingTensor input, TargetingChain chain) {
        TargetingTensor output = forward(chain);
        float[] inputValues = input.getValues();
        float[] outputValues = output.getValues();
        List<Float> targetList = system.chainToVector(chain);
        float[] targetValues = new float[targetList.size()];
        for (int i = 0; i < targetList.size(); i++) {
            targetValues[i] = targetList.get(i);
        }

        for (int i = 0; i < inputValues.length; i++) {
            for (int j = 0; j < inputValues.length; j++) {
                float gradient = inputValues[i] * (outputValues[j] - targetValues[j]) * outputValues[j] * (1 - outputValues[j]);
                float currentWeight = tensor.getValue(new int[]{i, j});
                tensor.setValue(new int[]{i, j}, currentWeight - learningRate * gradient);
            }
        }
    }
    
    private double calculateLoss(TargetingTensor predicted, TargetingChain actual) {
        float[] predictedValues = predicted.getValues();
        List<Float> actualList = system.chainToVector(actual);
        float[] actualValues = new float[actualList.size()];
        for (int i = 0; i < actualList.size(); i++) {
            actualValues[i] = actualList.get(i);
        }
        
        double loss = 0.0;
        for (int i = 0; i < system.getVectorSize(); i++) {
            double diff = predictedValues[i] - actualValues[i];
            loss += diff * diff;
        }
        
        return loss / system.getVectorSize();
    }
    
    private float sigmoid(float x) {
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }
    
    public void saveModel() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
            oos.writeObject(tensor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadModel() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath))) {
            tensor = (TargetingTensor) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public TargetingTensor getTensor() {
        return tensor;
    }

    @Override
    public List<TargetingComponent> getComponents() {
        return system.getComponents();
    }

    @Override
    public Set<TargetingComponent> getComponentsAsSet() {
        return new HashSet<>(getComponents());
    }

    @Override
    public Map<ComponentCategory, Set<TargetingComponent>> getValidComponents() {
        return system.getValidComponents();
    }

    public Set<TargetingComponent> convertToComponents(Set<String> values, ComponentCategory category) {
        Set<TargetingComponent> components = new HashSet<>();
        for (String value : values) {
            components.add(new TargetingComponent(category, value));
        }
        return components;
    }

    public List<TargetingComponent> convertToComponentList(Set<String> values, ComponentCategory category) {
        return new ArrayList<>(convertToComponents(values, category));
    }

    @Override
    public Set<TargetingComponent> getComponentsByCategory(ComponentCategory category) {
        return system.getComponentsByCategory(category);
    }

    @Override
    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        return new ArrayList<>(getComponentsByCategory(category));
    }

    @Override
    public List<TargetingComponent> getComponentsAsList() {
        return new ArrayList<>(getComponents());
    }
}

@Getter
@AllArgsConstructor
class TrainingMetrics implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int epoch;
    public double trainingLoss;
    public double validationLoss;
    public Map<ComponentCategory, Double> categoryLosses;
    public double patternComplexity;
} 