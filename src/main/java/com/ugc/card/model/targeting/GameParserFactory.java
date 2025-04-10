package com.ugc.card.model.targeting;

import java.util.*;

/**
 * Factory class for managing game-specific ability parsers.
 * This class provides a centralized way to access parsers for different games.
 */
public class GameParserFactory {
    private static GameParserFactory instance;
    private final TargetingSystem targetingSystem;
    private final Map<String, GameAbilityParser> parsers;
    
    public static GameParserFactory getInstance(TargetingSystem targetingSystem) {
        if (instance == null) {
            instance = new GameParserFactory(targetingSystem);
        }
        return instance;
    }
    
    public GameParserFactory(TargetingSystem targetingSystem) {
        this.targetingSystem = targetingSystem;
        this.parsers = new HashMap<>();
        initializeParsers();
    }
    
    private void initializeParsers() {
        parsers.put("snap", (GameAbilityParser) new SnapAbilityParser());
        parsers.put("starwars", (GameAbilityParser) new StarWarsAbilityParser());
        parsers.put("pokemon", (GameAbilityParser) new PokemonAbilityParser());
        parsers.put("yugioh", (GameAbilityParser) new YugiohAbilityParser());
        parsers.put("magic", (GameAbilityParser) new MagicAbilityParser());
        parsers.put("lorcana", (GameAbilityParser) new LorcanaAbilityParser());
        parsers.put("fleshandblood", (GameAbilityParser) new FleshAndBloodAbilityParser());
    }
    
    /**
     * Gets a parser for the specified game.
     * 
     * @param game The name of the game (case-insensitive)
     * @return The game-specific ability parser
     * @throws IllegalArgumentException if no parser is available for the specified game
     */
    public GameAbilityParser getParser(String game) {
        if (game == null) {
            throw new IllegalArgumentException("Game name cannot be null");
        }
        
        GameAbilityParser parser = parsers.get(game.toLowerCase());
        if (parser == null) {
            throw new IllegalArgumentException("No parser available for game: " + game);
        }
        
        return parser;
    }
    
    /**
     * Registers a new parser for a game.
     * 
     * @param game The name of the game (case-insensitive)
     * @param parser The parser to register
     * @throws IllegalArgumentException if game or parser is null
     */
    public void registerParser(String game, GameAbilityParser parser) {
        if (game == null) {
            throw new IllegalArgumentException("Game name cannot be null");
        }
        if (parser == null) {
            throw new IllegalArgumentException("Parser cannot be null");
        }
        
        parsers.put(game.toLowerCase(), parser);
    }
    
    /**
     * Checks if a parser is available for the specified game.
     * 
     * @param game The name of the game (case-insensitive)
     * @return true if a parser is available, false otherwise
     */
    public boolean isGameSupported(String game) {
        return game != null && parsers.containsKey(game.toLowerCase());
    }
    
    /**
     * Gets an array of all games that have registered parsers.
     * 
     * @return An array of game names
     */
    public String[] getAvailableGames() {
        return parsers.keySet().toArray(new String[0]);
    }

    /**
     * Removes a parser for the specified game.
     * 
     * @param game The name of the game (case-insensitive)
     * @return true if a parser was removed, false if no parser was registered for the game
     */
    public boolean removeParser(String game) {
        return game != null && parsers.remove(game.toLowerCase()) != null;
    }

    /**
     * Clears all registered parsers.
     */
    public void clearParsers() {
        parsers.clear();
    }

    public Set<String> getSupportedGames() {
        return new HashSet<>(parsers.keySet());
    }

    public static AbilityParser createParser(String gameType) {
        switch (gameType.toLowerCase()) {
            case "snap":
                return (AbilityParser) new SnapAbilityParser();
            case "lorcana":
                return (AbilityParser) new LorcanaAbilityParser();
            case "yugioh":
                return (AbilityParser) new YugiohAbilityParser();
            case "starwars":
                return (AbilityParser) new StarWarsAbilityParser();
            case "pokemon":
                return (AbilityParser) new PokemonAbilityParser();
            default:
                throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }
    }

    // Private constructor to prevent instantiation
    private GameParserFactory() {
        throw new AssertionError("GameParserFactory is a utility class and should not be instantiated");
    }
} 