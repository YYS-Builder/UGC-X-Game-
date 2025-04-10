package com.ugc.card.model.targeting;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class TargetingSystemTest {
    private TargetingSystem targetingSystem;
    
    @BeforeEach
    void setUp() {
        targetingSystem = new TargetingSystem();
    }
    
    @Test
    void testParseSimpleAbility() {
        String ability = "Deal 2 damage to target creature";
        List<TargetingChain> chains = targetingSystem.parseAbility(ability);
        
        assertNotNull(chains);
        assertEquals(1, chains.size());
        
        TargetingChain chain = chains.get(0);
        assertEquals(3, chain.getComponents().size());
        
        TargetingComponent base = chain.getComponents().get(0);
        assertEquals("Deal", base.getValue());
        assertEquals(TargetingComponent.ComponentCategory.BASE, base.getCategory());
        
        TargetingComponent type = chain.getComponents().get(1);
        assertEquals("damage", type.getValue());
        assertEquals(TargetingComponent.ComponentCategory.TYPE, type.getCategory());
        
        TargetingComponent target = chain.getComponents().get(2);
        assertEquals("creature", target.getValue());
        assertEquals(TargetingComponent.ComponentCategory.TARGET, target.getCategory());
    }
    
    @Test
    void testGenerateAbility() {
        TargetingChain chain = new TargetingChain();
        chain.addComponent(new TargetingComponent("Deal", TargetingComponent.ComponentCategory.BASE));
        chain.addComponent(new TargetingComponent("damage", TargetingComponent.ComponentCategory.TYPE));
        chain.addComponent(new TargetingComponent("creature", TargetingComponent.ComponentCategory.TARGET));
        
        String ability = targetingSystem.generateAbility(Collections.singletonList(chain));
        assertEquals("Deal damage to target creature", ability);
    }
    
    @Test
    void testParseComplexAbility() {
        String ability = "Deal 3 damage to target creature and draw a card";
        List<TargetingChain> chains = targetingSystem.parseAbility(ability);
        
        assertNotNull(chains);
        assertEquals(2, chains.size());
        
        // First chain
        TargetingChain damageChain = chains.get(0);
        assertEquals(3, damageChain.getComponents().size());
        
        // Second chain
        TargetingChain drawChain = chains.get(1);
        assertEquals(2, drawChain.getComponents().size());
    }
    
    @Test
    void testGenerateComplexAbility() {
        List<TargetingChain> chains = new ArrayList<>();
        
        // Damage chain
        TargetingChain damageChain = new TargetingChain();
        damageChain.addComponent(new TargetingComponent("Deal", TargetingComponent.ComponentCategory.BASE));
        damageChain.addComponent(new TargetingComponent("damage", TargetingComponent.ComponentCategory.TYPE));
        damageChain.addComponent(new TargetingComponent("creature", TargetingComponent.ComponentCategory.TARGET));
        chains.add(damageChain);
        
        // Draw chain
        TargetingChain drawChain = new TargetingChain();
        drawChain.addComponent(new TargetingComponent("Draw", TargetingComponent.ComponentCategory.BASE));
        drawChain.addComponent(new TargetingComponent("card", TargetingComponent.ComponentCategory.TYPE));
        chains.add(drawChain);
        
        String ability = targetingSystem.generateAbility(chains);
        assertEquals("Deal damage to target creature and draw a card", ability);
    }
    
    @Test
    void testInvalidAbility() {
        String invalidAbility = "Invalid ability text";
        List<TargetingChain> chains = targetingSystem.parseAbility(invalidAbility);
        
        assertNotNull(chains);
        assertTrue(chains.isEmpty());
    }
    
    @Test
    void testTargetingTrainer() {
        TargetingTrainer trainer = new TargetingTrainer(targetingSystem);
        
        // Test training
        assertDoesNotThrow(() -> {
            trainer.trainFromCSV("UGC Card Model/Out of Peters head - Build Table.csv");
        });
        
        // Test generation
        List<TargetingChain> generated = trainer.generateChains(5);
        assertNotNull(generated);
        assertEquals(5, generated.size());
        
        // Test saving/loading
        String modelPath = "UGC Card Model/data/models/test_model";
        assertDoesNotThrow(() -> {
            trainer.saveModel(modelPath);
            trainer.loadModel(modelPath);
        });
        
        trainer.close();
    }
    
    @Test
    void testTrainingDataGenerator() {
        TrainingDataGenerator generator = new TrainingDataGenerator(targetingSystem);
        
        String inputFile = "UGC Card Model/Out of Peters head - Build Table.csv";
        String outputFile = "UGC Card Model/data/training/test_training_data.csv";
        
        assertDoesNotThrow(() -> {
            generator.generateTrainingData(inputFile, outputFile);
        });
        
        // Verify output file exists
        assertTrue(new File(outputFile).exists());
    }
} 