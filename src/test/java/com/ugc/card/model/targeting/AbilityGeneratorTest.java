package com.ugc.card.model.targeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbilityGeneratorTest {
    private AbilityGenerator generator;
    private HearthstoneAbilityParser parser;

    @BeforeEach
    void setUp() {
        generator = new AbilityGenerator();
        parser = new HearthstoneAbilityParser();
    }

    @Test
    void testGenerateSimpleAbility() {
        TargetingChain chain = parser.parseAbility("deal 3 damage to target minion");
        String text = generator.generateText(chain);
        assertEquals("Deal 3 damage to target minion", text);
    }

    @Test
    void testGenerateBattlecryAbility() {
        TargetingChain chain = parser.parseAbility("Battlecry: Deal 2 damage to all minions");
        String text = generator.generateText(chain);
        assertEquals("Battlecry: Deal 2 damage to all minions", text);
    }

    @Test
    void testGenerateDeathrattleAbility() {
        TargetingChain chain = parser.parseAbility("Deathrattle: Draw 2 cards");
        String text = generator.generateText(chain);
        assertEquals("Deathrattle: Draw 2 cards", text);
    }

    @Test
    void testGenerateStateAbility() {
        TargetingChain chain = parser.parseAbility("deal 4 damage to damaged minions");
        String text = generator.generateText(chain);
        assertEquals("Deal 4 damage to damaged minions", text);
    }

    @Test
    void testGenerateEmptyChain() {
        TargetingChain chain = new TargetingChain();
        String text = generator.generateText(chain);
        assertTrue(text.isEmpty());
    }

    @Test
    void testGenerateComplexAbility() {
        TargetingChain chain = parser.parseAbility("Battlecry: If you have a dragon, deal 3 damage to all enemy minions");
        String text = generator.generateText(chain);
        assertEquals("Battlecry: If you have a dragon, deal 3 damage to all enemy minions", text);
    }

    @Test
    void testGenerateCustomTemplate() {
        generator.addEffectTemplate("summon", "{VALUE} {TYPE}");
        TargetingChain chain = parser.parseAbility("summon 2 minions");
        String text = generator.generateText(chain);
        assertEquals("Summon 2 minions", text);
    }

    @Test
    void testGenerateWithTriggerAndCondition() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TRIGGER, "battlecry"));
        chain.addComponent(new TargetingComponent(ComponentCategory.CONDITION, "if you have a dragon"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "3"));
        chain.addComponent(new TargetingComponent(ComponentCategory.DIRECTION, "all"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "minion"));
        
        String text = generator.generateText(chain);
        assertEquals("Battlecry: If you have a dragon, deal 3 damage to all minions", text);
    }

    @Test
    void testGenerateWithState() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        chain.addComponent(new TargetingComponent(ComponentCategory.STATE, "frozen"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "minion"));
        
        String text = generator.generateText(chain);
        assertEquals("Deal 2 damage to frozen minions", text);
    }

    @Test
    void testGenerateWithMultipleEffects() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TRIGGER, "battlecry"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "draw"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "2"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "deal"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "3"));
        chain.addComponent(new TargetingComponent(ComponentCategory.TYPE, "minion"));
        
        String text = generator.generateText(chain);
        assertEquals("Battlecry: Draw 2 cards and deal 3 damage to minions", text);
    }

    @Test
    void testGenerateWithCustomTemplates() {
        generator.addTriggerTemplate("inspire", "Inspire: {EFFECT}");
        generator.addEffectTemplate("restore", "restore {VALUE} Health");
        
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent(ComponentCategory.TRIGGER, "inspire"));
        chain.addComponent(new TargetingComponent(ComponentCategory.EFFECT, "restore"));
        chain.addComponent(new TargetingComponent(ComponentCategory.VALUE, "3"));
        
        String text = generator.generateText(chain);
        assertEquals("Inspire: Restore 3 Health", text);
    }
} 