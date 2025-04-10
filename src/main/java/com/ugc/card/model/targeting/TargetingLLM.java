package com.ugc.card.model.targeting;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetingLLM {
    private TargetingSystem system;
    private Map<String, Integer> wordToIndex;
    private Map<Integer, String> indexToWord;
    private int maxSequenceLength = 100;
    private Random random;
    
    public TargetingLLM(TargetingSystem system) {
        this.system = system;
        this.wordToIndex = new HashMap<>();
        this.indexToWord = new HashMap<>();
        this.random = new Random();
        initializeVocabulary();
    }
    
    private void initializeVocabulary() {
        // Add special tokens
        wordToIndex.put("[PAD]", 0);
        wordToIndex.put("[UNK]", 1);
        wordToIndex.put("[CLS]", 2);
        wordToIndex.put("[SEP]", 3);
        indexToWord.put(0, "[PAD]");
        indexToWord.put(1, "[UNK]");
        indexToWord.put(2, "[CLS]");
        indexToWord.put(3, "[SEP]");
        
        // Add component values
        int index = 4;
        for (TargetingComponent component : system.getComponents()) {
            if (!wordToIndex.containsKey(component.getText())) {
                wordToIndex.put(component.getText(), index);
                indexToWord.put(index, component.getText());
                index++;
            }
        }
    }
    
    public TargetingChain parseText(String text) {
        TargetingChain chain = new TargetingChain();
        text = text.toLowerCase().trim();
        
        // Extract effect first as it's the core of the ability
        TargetingComponent effectComponent = null;
        for (TargetingComponent component : system.getComponents()) {
            if (component.getCategory() == ComponentCategory.EFFECT) {
                String componentText = component.getText().toLowerCase();
                if (text.contains(componentText)) {
                    effectComponent = component;
                    chain.addComponent(component);
                    break;
                }
            }
        }
        
        if (effectComponent == null) {
            return null;
        }
        
        String effect = effectComponent.getText().toLowerCase();
        
        // Extract type based on effect context
        boolean needsSpellType = effect.equals("counter");
        for (TargetingComponent component : system.getComponents()) {
            if (component.getCategory() == ComponentCategory.TYPE) {
                String componentText = component.getText().toLowerCase();
                if (text.contains(componentText)) {
                    // Validate type-effect relationship
                    if (needsSpellType) {
                        if (componentText.equals("spell") || componentText.equals("ability")) {
                            chain.addComponent(component);
                            break;
                        }
                    } else {
                        if (!componentText.equals("spell") && !componentText.equals("ability")) {
                            chain.addComponent(component);
                            break;
                        }
                    }
                }
            }
        }
        
        // Extract direction/targeting modifiers
        boolean canBeRandom = effect.equals("counter") || effect.equals("discard");
        for (TargetingComponent component : system.getComponents()) {
            if (component.getCategory() == ComponentCategory.DIRECTION) {
                String componentText = component.getText().toLowerCase();
                if (text.contains(componentText)) {
                    // Validate direction-effect relationship
                    if (componentText.equals("random") && !canBeRandom) {
                        continue;
                    }
                    if (componentText.equals("another")) {
                        // Check if we have a valid type for "another"
                        boolean hasValidType = chain.getComponents().stream()
                            .filter(c -> c.getCategory() == ComponentCategory.TYPE)
                            .anyMatch(c -> {
                                String type = c.getText().toLowerCase();
                                return type.equals("creature") || 
                                       type.equals("artifact") || 
                                       type.equals("enchantment");
                            });
                        if (!hasValidType) {
                            continue;
                        }
                    }
                    chain.addComponent(component);
                }
            }
        }
        
        // Extract numerical values
        boolean needsValue = effect.equals("damage") || effect.equals("heal") || 
                           effect.equals("draw") || effect.equals("mill");
        
        if (needsValue) {
            // Try to find explicit numbers
            Pattern numberPattern = Pattern.compile("\\b(\\d+|X)\\b");
            Matcher matcher = numberPattern.matcher(text);
            if (matcher.find()) {
                String value = matcher.group(1);
                // Validate value range
                try {
                    int numValue = Integer.parseInt(value);
                    if (numValue > 0 && numValue <= 15) {
                        for (TargetingComponent component : system.getComponents()) {
                            if (component.getCategory() == ComponentCategory.VALUE && 
                                component.getText().equals(value)) {
                                chain.addComponent(component);
                                break;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Handle X value
                    if (value.equalsIgnoreCase("X")) {
                        for (TargetingComponent component : system.getComponents()) {
                            if (component.getCategory() == ComponentCategory.VALUE && 
                                component.getText().equalsIgnoreCase("X")) {
                                chain.addComponent(component);
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        return chain.getComponents().size() >= 2 ? chain : null;
    }
    
    public String generateText(TargetingChain chain) {
        StringBuilder text = new StringBuilder();
        for (TargetingComponent component : chain.getComponents()) {
            if (text.length() > 0) {
                text.append(" ");
            }
            text.append(component.getText());
        }
        return text.toString();
    }
    
    public List<Integer> tokenize(String text) {
        List<Integer> tokens = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            tokens.add(wordToIndex.getOrDefault(word, wordToIndex.get("[UNK]")));
        }
        return tokens;
    }
    
    public String detokenize(List<Integer> tokens) {
        StringBuilder text = new StringBuilder();
        for (Integer token : tokens) {
            String word = indexToWord.get(token);
            if (!word.startsWith("[") && !word.endsWith("]")) {
                if (text.length() > 0) {
                    text.append(" ");
                }
                text.append(word);
            }
        }
        return text.toString();
    }
    
    public TargetingChain generateRandomChain() {
        TargetingChain chain = new TargetingChain();
        int numComponents = random.nextInt(5) + 1; // 1-5 components
        List<TargetingComponent> availableComponents = new ArrayList<>(system.getComponents());
        for (int i = 0; i < numComponents; i++) {
            if (availableComponents.isEmpty()) {
                break;
            }
            int index = random.nextInt(availableComponents.size());
            chain.addComponent(availableComponents.get(index));
            availableComponents.remove(index);
        }
        return chain;
    }
} 