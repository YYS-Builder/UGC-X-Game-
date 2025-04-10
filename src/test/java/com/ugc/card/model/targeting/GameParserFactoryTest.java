package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameParserFactoryTest {
    private GameAbilityParser testParser;

    @BeforeEach
    void setUp() {
        // Clear any existing parsers
        GameParserFactory.clearParsers();

        // Create a test parser
        testParser = new GameAbilityParser() {
            @Override
            public TargetingChain parseAbility(String text) {
                return new TargetingChain();
            }

            @Override
            public boolean isValidChain(TargetingChain chain) {
                return true;
            }

            @Override
            public String generateText(TargetingChain chain) {
                return "";
            }

            @Override
            public Set<ComponentCategory> getValidCategories() {
                return Collections.emptySet();
            }

            @Override
            public Set<String> getValidComponents(ComponentCategory category) {
                return Collections.emptySet();
            }
        };
    }

    @Test
    void testRegisterAndGetParser() {
        GameParserFactory.registerParser("test", testParser);
        
        GameAbilityParser retrieved = GameParserFactory.getParser("test");
        assertNotNull(retrieved);
        assertSame(testParser, retrieved);
    }

    @Test
    void testRegisterParserCaseInsensitive() {
        GameParserFactory.registerParser("TEST", testParser);
        
        GameAbilityParser retrieved = GameParserFactory.getParser("test");
        assertNotNull(retrieved);
        assertSame(testParser, retrieved);
    }

    @Test
    void testRegisterNullGame() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameParserFactory.registerParser(null, testParser);
        });
    }

    @Test
    void testRegisterNullParser() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameParserFactory.registerParser("test", null);
        });
    }

    @Test
    void testGetParserForUnregisteredGame() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameParserFactory.getParser("nonexistent");
        });
    }

    @Test
    void testGetParserWithNullGame() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameParserFactory.getParser(null);
        });
    }

    @Test
    void testHasParser() {
        GameParserFactory.registerParser("test", testParser);
        
        assertTrue(GameParserFactory.hasParser("test"));
        assertTrue(GameParserFactory.hasParser("TEST"));
        assertFalse(GameParserFactory.hasParser("nonexistent"));
    }

    @Test
    void testHasParserWithNull() {
        assertFalse(GameParserFactory.hasParser(null));
    }

    @Test
    void testGetAvailableGames() {
        GameParserFactory.registerParser("test1", testParser);
        GameParserFactory.registerParser("test2", testParser);
        
        String[] games = GameParserFactory.getAvailableGames();
        assertEquals(2, games.length);
        assertTrue(Arrays.asList(games).contains("test1"));
        assertTrue(Arrays.asList(games).contains("test2"));
    }

    @Test
    void testGetAvailableGamesEmpty() {
        String[] games = GameParserFactory.getAvailableGames();
        assertEquals(0, games.length);
    }

    @Test
    void testRemoveParser() {
        GameParserFactory.registerParser("test", testParser);
        assertTrue(GameParserFactory.hasParser("test"));
        
        boolean removed = GameParserFactory.removeParser("test");
        assertTrue(removed);
        assertFalse(GameParserFactory.hasParser("test"));
    }

    @Test
    void testRemoveNonexistentParser() {
        boolean removed = GameParserFactory.removeParser("nonexistent");
        assertFalse(removed);
    }

    @Test
    void testRemoveNullParser() {
        boolean removed = GameParserFactory.removeParser(null);
        assertFalse(removed);
    }

    @Test
    void testClearParsers() {
        GameParserFactory.registerParser("test1", testParser);
        GameParserFactory.registerParser("test2", testParser);
        
        GameParserFactory.clearParsers();
        assertEquals(0, GameParserFactory.getAvailableGames().length);
    }

    @Test
    void testPrivateConstructor() {
        assertThrows(AssertionError.class, () -> {
            GameParserFactory factory = GameParserFactory.class.getDeclaredConstructor().newInstance();
        });
    }

    @Test
    void testDefaultParsers() {
        // Clear any existing parsers
        GameParserFactory.clearParsers();
        
        // Re-register default parsers
        GameParserFactory.registerParser("magic", new MagicAbilityParser());
        GameParserFactory.registerParser("hearthstone", new HearthstoneAbilityParser());
        
        assertTrue(GameParserFactory.hasParser("magic"));
        assertTrue(GameParserFactory.hasParser("hearthstone"));
        
        assertNotNull(GameParserFactory.getParser("magic"));
        assertNotNull(GameParserFactory.getParser("hearthstone"));
        
        assertTrue(GameParserFactory.getParser("magic") instanceof MagicAbilityParser);
        assertTrue(GameParserFactory.getParser("hearthstone") instanceof HearthstoneAbilityParser);
    }
} 