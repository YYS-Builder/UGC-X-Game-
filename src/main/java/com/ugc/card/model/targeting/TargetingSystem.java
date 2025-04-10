package com.ugc.card.model.targeting;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.ugc.card.model.targeting.ComponentCategory;
import java.util.Set;
import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetingSystem {
    private Set<TargetingComponent> components = new HashSet<>();
    private Map<String, TargetingComponent> componentMap = new HashMap<>();
    private Map<ComponentCategory, Set<TargetingComponent>> componentsByCategory = new HashMap<>();
    private Map<ComponentCategory, Set<String>> validValues = new HashMap<>();
    private Map<String, GameAbilityParser> gameParsers = new HashMap<>();
    private TargetingTensor model;
    private int vectorSize = 100; // Default vector size
    private Map<String, Set<ComponentCategory>> gameCategories = new HashMap<>();
    private Map<String, Map<ComponentCategory, Set<String>>> gameComponents = new HashMap<>();
    private Map<String, Set<ValidationRule>> gameRules = new HashMap<>();
    private Map<String, Set<AbilityType>> gameAbilityTypes = new HashMap<>();
    private Map<String, Set<TargetingPattern>> gamePatterns = new HashMap<>();
    private Random random = new Random();
    private Set<ValidationRule> validationRules = new HashSet<>();
    private Set<TargetingPattern> targetingPatterns = new HashSet<>();
    
    public TargetingSystem(Set<TargetingComponent> components,
                         Map<String, TargetingComponent> componentMap,
                         Map<ComponentCategory, Set<TargetingComponent>> componentsByCategory,
                         Map<ComponentCategory, Set<String>> validValues,
                         Map<String, GameAbilityParser> gameParsers,
                         TargetingTensor model,
                         Map<String, Set<ComponentCategory>> gameCategories,
                         Map<String, Map<ComponentCategory, Set<String>>> gameComponents,
                         Map<String, Set<ValidationRule>> gameRules,
                         Map<String, Set<AbilityType>> gameAbilityTypes,
                         Map<String, Set<TargetingPattern>> gamePatterns,
                         Set<ValidationRule> validationRules,
                         Set<TargetingPattern> targetingPatterns) {
        this.components = components;
        this.componentMap = componentMap;
        this.componentsByCategory = componentsByCategory;
        this.validValues = validValues;
        this.gameParsers = gameParsers;
        this.model = model;
        this.gameCategories = gameCategories;
        this.gameComponents = gameComponents;
        this.gameRules = gameRules;
        this.gameAbilityTypes = gameAbilityTypes;
        this.gamePatterns = gamePatterns;
        this.validationRules = validationRules;
        this.targetingPatterns = targetingPatterns;
        this.random = new Random();
        
        initializeGameParsers();
        initializeGames();
    }
    
    private void initializeGameParsers() {
        gameParsers.put("snap", (GameAbilityParser) new SnapAbilityParser());
        gameParsers.put("starwars", (GameAbilityParser) new StarWarsAbilityParser());
        gameParsers.put("pokemon", (GameAbilityParser) new PokemonAbilityParser());
        gameParsers.put("yugioh", (GameAbilityParser) new YugiohAbilityParser());
        gameParsers.put("magic", (GameAbilityParser) new MagicAbilityParser());
        gameParsers.put("lorcana", (GameAbilityParser) new LorcanaAbilityParser());
        gameParsers.put("fleshandblood", (GameAbilityParser) new FleshAndBloodAbilityParser());
    }
    
    private void initializeGames() {
        // Initialize categories and components for each game
        for (Map.Entry<String, GameAbilityParser> entry : gameParsers.entrySet()) {
            String game = entry.getKey();
            GameAbilityParser parser = entry.getValue();
            
            // Store categories
            gameCategories.put(game, parser.getValidCategories());
            
            // Store components
            Map<ComponentCategory, Set<String>> components = new HashMap<>();
            for (ComponentCategory category : parser.getValidCategories()) {
                Set<String> validValues = parser.getValidComponents(category);
                components.put(category, validValues);
            }
            gameComponents.put(game, components);
            
            // Store rules
            gameRules.put(game, parser.getValidationRules());
            
            // Store ability types
            gameAbilityTypes.put(game, parser.getAbilityTypes());
            
            // Store patterns
            gamePatterns.put(game, parser.getTargetingPatterns());
        }
    }
    
    public void addComponent(TargetingComponent component) {
        components.add(component);
        componentMap.put(component.getValue(), component);
        componentsByCategory.get(component.getCategory()).add(component);
        vectorSize++;
    }
    
    public Set<TargetingComponent> getComponents() {
        return new HashSet<>(components);
    }
    
    public Set<TargetingComponent> getComponentsByCategory(ComponentCategory category) {
        Set<String> values = componentValues.get(category);
        if (values == null) {
            return new HashSet<>();
        }
        return values.stream()
            .map(value -> componentMap.get(value))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
    
    public List<TargetingComponent> getComponentsByCategoryAsList(ComponentCategory category) {
        return new ArrayList<>(getComponentsByCategory(category));
    }
    
    public TargetingComponent getComponentByValue(String value) {
        return componentMap.get(value);
    }
    
    public int getComponentIndex(TargetingComponent component) {
        int index = 0;
        for (TargetingComponent c : components) {
            if (c.equals(component)) {
                return index;
            }
            index++;
        }
        return -1;
    }
    
    public TargetingComponent getComponentAtIndex(int index) {
        int currentIndex = 0;
        for (TargetingComponent component : components) {
            if (currentIndex == index) {
                return component;
            }
            currentIndex++;
        }
        return null;
    }
    
    public TargetingChain createChain() {
        return new TargetingChain();
    }
    
    public TargetingChain parseSentence(String sentence) {
        TargetingChain chain = new TargetingChain();
        sentence = sentence.toLowerCase();
        
        // 1. Keyword Abilities
        if (parseKeywordAbility(sentence, chain)) {
            return chain;
        }
        
        // 2. Triggered Abilities
        if (parseTriggeredAbility(sentence, chain)) {
            return chain;
        }
        
        // 3. Activated Abilities
        if (parseActivatedAbility(sentence, chain)) {
            return chain;
        }
        
        // 4. Static Abilities
        if (parseStaticAbility(sentence, chain)) {
            return chain;
        }
        
        // 5. Replacement Effects
        if (parseReplacementEffect(sentence, chain)) {
            return chain;
        }
        
        // 6. State-Based Actions
        if (parseStateBasedAction(sentence, chain)) {
            return chain;
        }
        
        // Default parsing for other cases
        return parseDefaultAbility(sentence, chain);
    }
    
    private boolean parseKeywordAbility(String sentence, TargetingChain chain) {
        // Combat Keywords
        if (sentence.contains("trample")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "damage"));
            chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "leftover"));
            chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "player"));
            chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "redirect"));
            return true;
        }
        
        if (sentence.contains("lifelink")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "damage"));
            chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "controller"));
            chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "heal"));
            return true;
        }
        
        if (sentence.contains("deathtouch")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "damage"));
            chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "any"));
            chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "destroy"));
            return true;
        }
        
        if (sentence.contains("first strike") || sentence.contains("double strike")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "damage"));
            chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "first"));
            if (sentence.contains("double strike")) {
                chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "second"));
            }
            chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
            return true;
        }
        
        // Evasion Keywords
        if (sentence.contains("flying")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "blocking"));
            chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "flying"));
            chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "only"));
            return true;
        }
        
        if (sentence.contains("reach")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "blocking"));
            chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "flying"));
            chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "can"));
            return true;
        }
        
        // Protection Keywords
        if (sentence.contains("hexproof")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "spell"));
            chain.addComponent(new TargetingComponent(ComponentCategory.TARGET, "opponent"));
            chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "can't"));
            chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "target"));
            return true;
        }
        
        if (sentence.contains("protection from")) {
            String[] parts = sentence.split("protection from");
            if (parts.length > 1) {
                String protectedFrom = parts[1].trim();
                chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, protectedFrom));
                chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "can't"));
                chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "target"));
                chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "damage"));
                chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "prevent"));
                return true;
            }
        }
        
        return false;
    }
    
    private boolean parseTriggeredAbility(String sentence, TargetingChain chain) {
        // Handle "when", "whenever", "at" triggers
        if (sentence.startsWith("when") || sentence.startsWith("whenever") || sentence.startsWith("at")) {
            // Extract trigger condition
            String trigger = extractTriggerCondition(sentence);
            if (trigger != null) {
                chain.addComponent(new TargetingComponent(ComponentCategory.STATE, trigger));
                return true;
            }
        }
        return false;
    }
    
    private boolean parseActivatedAbility(String sentence, TargetingChain chain) {
        // Handle activated abilities (cost: effect)
        if (sentence.contains(":")) {
            String[] parts = sentence.split(":");
            if (parts.length == 2) {
                String cost = parts[0].trim();
                String effect = parts[1].trim();
                
                // Add cost component
                chain.addComponent(new TargetingComponent(ComponentCategory.COST, cost));
                
                // Parse effect
                return parseEffect(effect, chain);
            }
        }
        return false;
    }
    
    private boolean parseStaticAbility(String sentence, TargetingChain chain) {
        // Handle static abilities (continuous effects)
        if (sentence.contains("gets") || sentence.contains("have")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "static"));
            return true;
        }
        return false;
    }
    
    private boolean parseReplacementEffect(String sentence, TargetingChain chain) {
        // Handle replacement effects
        if (sentence.contains("instead")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "replacement"));
            return true;
        }
        return false;
    }
    
    private boolean parseStateBasedAction(String sentence, TargetingChain chain) {
        // Handle state-based actions
        if (sentence.contains("if") && sentence.contains("then")) {
            chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "state-based"));
            return true;
        }
        return false;
    }
    
    private TargetingChain parseDefaultAbility(String sentence, TargetingChain chain) {
        // Default parsing for other cases
        String[] words = sentence.split("\\s+");
        for (String word : words) {
            // Check for numbers
            if (word.matches("\\d+")) {
                chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, word));
            }
            // Check for types
            else if (isType(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, word));
            }
            // Check for directions
            else if (isDirection(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, word));
            }
            // Check for effects
            else if (isEffect(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, word));
            }
        }
        return chain;
    }
    
    private String extractTriggerCondition(String sentence) {
        // Extract trigger condition from sentence
        Pattern pattern = Pattern.compile("(when|whenever|at)\\s+(.*?)(,|\\s+then|$)");
        Matcher matcher = pattern.matcher(sentence);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }
    
    private boolean parseEffect(String effect, TargetingChain chain) {
        // Parse effect text
        String[] words = effect.split("\\s+");
        for (String word : words) {
            if (isEffect(word)) {
                chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, word));
                return true;
            }
        }
        return false;
    }
    
    private boolean isType(String word) {
        Set<String> validTypes = componentsByCategory.get(ComponentCategory.TYPE);
        return validTypes != null && validTypes.contains(word.toLowerCase());
    }
    
    private boolean isDirection(String word) {
        Set<String> validDirections = componentsByCategory.get(ComponentCategory.DIRECTION);
        return validDirections != null && validDirections.contains(word.toLowerCase());
    }
    
    private boolean isEffect(String word) {
        Set<String> validEffects = componentsByCategory.get(ComponentCategory.EFFECT);
        return validEffects != null && validEffects.contains(word.toLowerCase());
    }
    
    public TargetingTensor encodeChain(TargetingChain chain) {
        int[] shape = {components.size()};
        float[] data = new float[components.size()];
        
        for (TargetingComponent component : chain.getComponents()) {
            int index = getComponentIndex(component);
            if (index >= 0) {
                data[index] = 1.0f;
            }
        }
        
        return new TargetingTensor(data, shape);
    }
    
    public TargetingChain decodeChain(TargetingTensor tensor) {
        TargetingChain chain = new TargetingChain();
        float[] data = tensor.getData();
        
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                TargetingComponent component = getComponentAtIndex(i);
                if (component != null) {
                    chain.addComponent(component);
                }
            }
        }
        
        return chain;
    }
    
    public void train(List<TargetingChain> chains) {
        int[] shape = new int[]{components.size()};
        model = new TargetingTensor(shape);
        float[] values = new float[components.size()];
        for (TargetingChain chain : chains) {
            TargetingTensor encoded = encodeChain(chain);
            float[] encodedValues = encoded.getValues();
            for (int i = 0; i < values.length; i++) {
                values[i] += encodedValues[i];
            }
        }
        for (int i = 0; i < values.length; i++) {
            values[i] /= chains.size();
        }
        model.setValues(values);
    }
    
    public List<TargetingChain> generateChains(int count) {
        List<TargetingChain> chains = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float[] noise = new float[components.size()];
            for (int j = 0; j < noise.length; j++) {
                noise[j] = (float) random.nextDouble();
            }
            TargetingTensor noiseTensor = new TargetingTensor(new int[]{components.size()});
            noiseTensor.setValues(noise);
            
            float[] modelValues = model.getValues();
            float[] generatedValues = new float[components.size()];
            for (int j = 0; j < generatedValues.length; j++) {
                generatedValues[j] = modelValues[j] + noise[j];
            }
            
            TargetingTensor generated = new TargetingTensor(new int[]{components.size()});
            generated.setValues(generatedValues);
            chains.add(decodeChain(generated));
        }
        return chains;
    }
    
    public List<TargetingChain> parseAbility(String text) {
        List<TargetingChain> chains = new ArrayList<>();
        
        // Split into sentences
        String[] sentences = text.split("[.!?]");
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                chains.add(parseSentence(sentence.trim()));
            }
        }
        
        return chains;
    }
    
    public String generateAbility(List<TargetingChain> chains) {
        StringBuilder ability = new StringBuilder();
        
        for (TargetingChain chain : chains) {
            if (ability.length() > 0) {
                ability.append(". ");
            }
            
            // Convert chain to tensor
            TargetingTensor chainTensor = encodeChain(chain);
            
            // Generate text from tensor
            ability.append(generateText(chainTensor));
        }
        
        return ability.toString();
    }
    
    public List<Float> chainToVector(TargetingChain chain) {
        List<Float> vector = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            vector.add(0.0f);
        }
        for (TargetingComponent component : chain.getComponents()) {
            int index = getComponentIndex(component);
            if (index >= 0 && index < components.size()) {
                vector.set(index, 1.0f);
            }
        }
        return vector;
    }
    
    private String generateText(TargetingTensor tensor) {
        StringBuilder text = new StringBuilder();
        List<TargetingComponent> activeComponents = getActiveComponents(tensor);
        
        // Add components in order: DIRECTION, TYPE, EFFECT, VALUE
        for (ComponentCategory category : new ComponentCategory[]{ComponentCategory.DIRECTION, ComponentCategory.TYPE, ComponentCategory.EFFECT, ComponentCategory.VALUE}) {
            for (TargetingComponent component : activeComponents) {
                if (component.getCategory() == category) {
                    if (text.length() > 0) {
                        text.append(" ");
                    }
                    text.append(component.getValue());
                }
            }
        }
        
        return text.toString();
    }
    
    private List<TargetingComponent> getActiveComponents(TargetingTensor tensor) {
        List<TargetingComponent> active = new ArrayList<>();
        float[] values = tensor.getValues();
        for (int i = 0; i < values.length; i++) {
            if (values[i] > 0.5f) {
                active.add(components.get(i));
            }
        }
        return active;
    }
    
    public int getVectorSize() {
        return components.size();
    }
    
    public TargetingTensor chainToTensor(TargetingChain chain) {
        TargetingTensor tensor = new TargetingTensor(new int[]{components.size()});
        float[] values = new float[components.size()];
        for (TargetingComponent component : chain.getComponents()) {
            int index = getComponentIndex(component);
            if (index >= 0 && index < components.size()) {
                values[index] = 1.0f;
            }
        }
        tensor.setValues(values);
        return tensor;
    }
    
    public double calculateSimilarity(TargetingChain a, TargetingChain b) {
        Set<ComponentCategory> categoriesA = a.getComponents().stream()
            .map(TargetingComponent::getCategory)
            .collect(Collectors.toSet());
        Set<ComponentCategory> categoriesB = b.getComponents().stream()
            .map(TargetingComponent::getCategory)
            .collect(Collectors.toSet());
        Set<ComponentCategory> intersection = new HashSet<>(categoriesA);
        intersection.retainAll(categoriesB);
        Set<ComponentCategory> union = new HashSet<>(categoriesA);
        union.addAll(categoriesB);
        return (double) intersection.size() / union.size();
    }

    public void registerParser(String game, GameAbilityParser parser) {
        gameParsers.put(game.toLowerCase(), parser);
        componentsByCategory.putAll(parser.getValidComponents());
        validationRules.addAll(parser.getValidationRules());
        targetingPatterns.addAll(parser.getTargetingPatterns());
    }

    public boolean validateChain(TargetingChain chain) {
        for (ValidationRule rule : validationRules) {
            if (!rule.validate(chain)) {
                return false;
            }
        }
        return true;
    }

    public Set<ComponentCategory> getValidCategories() {
        Set<ComponentCategory> validCategories = new HashSet<>();
        for (TargetingComponent component : components) {
            validCategories.add(component.getCategory());
        }
        return validCategories;
    }

    public Set<String> getValidComponents(ComponentCategory category) {
        Set<String> values = new HashSet<>();
        for (Map.Entry<String, GameAbilityParser> entry : gameParsers.entrySet()) {
            values.addAll(entry.getValue().getValidComponents(category));
        }
        return values;
    }

    public Set<TargetingPattern> getTargetingPatterns() {
        return targetingPatterns;
    }

    public Set<ValidationRule> getValidationRules() {
        return validationRules;
    }

    public TargetingChain parseAbility(String abilityText, String game) {
        GameAbilityParser parser = gameParsers.get(game.toLowerCase());
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        return parser.parseAbility(abilityText);
    }

    public boolean isValidAbility(String game, String abilityText) {
        GameAbilityParser parser = gameParsers.get(game.toLowerCase());
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        TargetingChain chain = parser.parseAbility(abilityText);
        return parser.isValidChain(chain);
    }

    public String generateAbilityText(String game, TargetingChain chain) {
        GameAbilityParser parser = gameParsers.get(game.toLowerCase());
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        return parser.generateText(chain);
    }

    public Set<ComponentCategory> getValidCategories(String game) {
        GameAbilityParser parser = gameParsers.get(game.toLowerCase());
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        return parser.getValidCategories();
    }

    public Set<String> getValidComponents(String game, ComponentCategory category) {
        GameAbilityParser parser = gameParsers.get(game);
        if (parser != null) {
            return parser.getValidComponents(category);
        }
        return Collections.emptySet();
    }

    public Map<ComponentCategory, Set<String>> getValidComponents(String game) {
        GameAbilityParser parser = gameParsers.get(game);
        if (parser != null) {
            return parser.getValidComponents();
        }
        return Collections.emptyMap();
    }

    public Set<ValidationRule> getValidationRules(String game) {
        Set<ValidationRule> rules = gameRules.get(game.toLowerCase());
        if (rules == null) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        return rules;
    }

    public Set<AbilityType> getAbilityTypes(String game) {
        Set<AbilityType> types = gameAbilityTypes.get(game.toLowerCase());
        if (types == null) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        return types;
    }

    public Set<TargetingPattern> getTargetingPatterns(String game) {
        Set<TargetingPattern> patterns = gamePatterns.get(game.toLowerCase());
        if (patterns == null) {
            throw new IllegalArgumentException("Unsupported game: " + game);
        }
        return patterns;
    }

    public void addGame(String game, GameAbilityParser parser) {
        String gameLower = game.toLowerCase();
        gameParsers.put(gameLower, parser);
        gameCategories.put(gameLower, parser.getValidCategories());
        
        Map<ComponentCategory, Set<String>> components = new HashMap<>();
        for (ComponentCategory category : parser.getValidCategories()) {
            components.put(category, parser.getValidComponents(category));
        }
        gameComponents.put(gameLower, components);
        
        gameRules.put(gameLower, parser.getValidationRules());
        gameAbilityTypes.put(gameLower, parser.getAbilityTypes());
        gamePatterns.put(gameLower, parser.getTargetingPatterns());
    }

    public void removeGame(String game) {
        String gameLower = game.toLowerCase();
        gameParsers.remove(gameLower);
        gameCategories.remove(gameLower);
        gameComponents.remove(gameLower);
        gameRules.remove(gameLower);
        gameAbilityTypes.remove(gameLower);
        gamePatterns.remove(gameLower);
    }

    public Set<String> getSupportedGames() {
        return new HashSet<>(gameParsers.keySet());
    }

    public boolean isGameSupported(String game) {
        return gameParsers.containsKey(game.toLowerCase());
    }

    public GameAbilityParser getGameParser(String game) {
        return gameParsers.get(game);
    }

    public TargetingComponent getComponent(int index) {
        List<TargetingComponent> componentList = new ArrayList<>(components);
        return index >= 0 && index < componentList.size() ? componentList.get(index) : null;
    }

    public List<TargetingComponent> getComponentsAsList() {
        return new ArrayList<>(components);
    }

    public Map<ComponentCategory, Set<String>> getValidValues() {
        Map<ComponentCategory, Set<String>> result = new HashMap<>();
        for (Map.Entry<ComponentCategory, Set<TargetingComponent>> entry : componentsByCategory.entrySet()) {
            Set<String> values = new HashSet<>();
            for (TargetingComponent component : entry.getValue()) {
                values.add(component.getValue());
            }
            result.put(entry.getKey(), values);
        }
        return result;
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

    public Set<String> getComponentValues(ComponentCategory category) {
        return componentValues.getOrDefault(category, new HashSet<>());
    }

    public Map<ComponentCategory, Set<TargetingComponent>> getValidComponents() {
        Map<ComponentCategory, Set<TargetingComponent>> result = new HashMap<>();
        for (Map.Entry<ComponentCategory, Set<String>> entry : componentValues.entrySet()) {
            result.put(entry.getKey(), getComponentsByCategory(entry.getKey()));
        }
        return result;
    }
} 