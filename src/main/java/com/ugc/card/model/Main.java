package com.ugc.card.model;

import com.ugc.card.model.targeting.*;
import java.util.HashSet;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        TargetingSystem system = new TargetingSystem(
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
        GameParserFactory factory = GameParserFactory.getInstance(system);
        
        // Demonstrate Magic: The Gathering ability parsing
        System.out.println("Magic: The Gathering Examples");
        System.out.println("-----------------------------");
        
        GameAbilityParser magicParser = factory.getParser("magic");
        
        // Example 1: Parse a standard ability
        String magicAbility1 = "Deal 3 damage to target creature";
        TargetingChain magicChain1 = magicParser.parseAbility(magicAbility1);
        System.out.println("Input: " + magicAbility1);
        System.out.println("Valid: " + magicParser.isValidChain(magicChain1));
        System.out.println("Generated: " + magicParser.generateText(magicChain1));
        System.out.println();
        
        // Example 2: Parse a keyword ability
        String magicAbility2 = "Trample";
        TargetingChain magicChain2 = magicParser.parseAbility(magicAbility2);
        System.out.println("Input: " + magicAbility2);
        System.out.println("Valid: " + magicParser.isValidChain(magicChain2));
        System.out.println("Generated: " + magicParser.generateText(magicChain2));
        System.out.println();
        
        // Demonstrate Hearthstone ability parsing
        System.out.println("Hearthstone Examples");
        System.out.println("-------------------");
        
        GameAbilityParser hearthstoneParser = factory.getParser("hearthstone");
        
        // Example 1: Parse a standard ability
        String hearthstoneAbility1 = "Deal 2 damage to all enemy minions";
        TargetingChain hearthstoneChain1 = hearthstoneParser.parseAbility(hearthstoneAbility1);
        System.out.println("Input: " + hearthstoneAbility1);
        System.out.println("Valid: " + hearthstoneParser.isValidChain(hearthstoneChain1));
        System.out.println("Generated: " + hearthstoneParser.generateText(hearthstoneChain1));
        System.out.println();
        
        // Example 2: Parse a keyword ability
        String hearthstoneAbility2 = "Battlecry: Deal 1 damage";
        TargetingChain hearthstoneChain2 = hearthstoneParser.parseAbility(hearthstoneAbility2);
        System.out.println("Input: " + hearthstoneAbility2);
        System.out.println("Valid: " + hearthstoneParser.isValidChain(hearthstoneChain2));
        System.out.println("Generated: " + hearthstoneParser.generateText(hearthstoneChain2));
        System.out.println();
        
        // Display available games
        System.out.println("Available Games");
        System.out.println("---------------");
        for (String game : factory.getSupportedGames()) {
            System.out.println("- " + game);
        }
        
        // Demonstrate component categories
        System.out.println("\nComponent Categories");
        System.out.println("-------------------");
        for (ComponentCategory category : ComponentCategory.values()) {
            System.out.println("- " + category);
        }
        
        // Demonstrate valid components for each game
        System.out.println("\nValid Components by Game");
        System.out.println("----------------------");
        
        System.out.println("\nMagic: The Gathering");
        for (ComponentCategory category : magicParser.getValidCategories()) {
            System.out.println("\n" + category + ":");
            for (String component : magicParser.getValidComponents(category)) {
                System.out.println("- " + component);
            }
        }
        
        System.out.println("\nHearthstone");
        for (ComponentCategory category : hearthstoneParser.getValidCategories()) {
            System.out.println("\n" + category + ":");
            for (String component : hearthstoneParser.getValidComponents(category)) {
                System.out.println("- " + component);
            }
        }
    }
} 