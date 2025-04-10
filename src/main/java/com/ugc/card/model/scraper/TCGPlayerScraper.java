package com.ugc.card.model.scraper;

import com.ugc.card.model.Card;
import com.ugc.card.model.targeting.TargetingSystem;
import com.ugc.card.model.targeting.TargetingLLM;
import com.ugc.card.model.targeting.TargetingChain;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.nio.file.*;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TCGPlayerScraper {
    private TargetingSystem system;
    private TargetingLLM llm;
    private Map<String, List<String>> gameAbilities;
    private Map<String, Map<String, Integer>> abilityPatterns;
    private String outputDir;
    private String baseUrl;
    private Map<String, String> headers;
    private List<String> cardUrls;
    private List<Card> cards;
    
    public TCGPlayerScraper(TargetingSystem system, TargetingLLM llm) {
        this.system = system;
        this.llm = llm;
        this.cardUrls = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.gameAbilities = new HashMap<>();
        this.abilityPatterns = new HashMap<>();
    }
    
    public void scrapeGame(String gameName, String baseUrl) {
        try {
            List<String> abilities = new ArrayList<>();
            Document doc = Jsoup.connect(baseUrl).get();
            
            // Extract card elements (this will need to be adjusted based on TCGPlayer's HTML structure)
            Elements cards = doc.select(".card-item");
            
            for (Element card : cards) {
                // Extract card name
                String cardName = card.select(".card-name").text();
                
                // Extract abilities
                Elements abilityElements = card.select(".card-text");
                for (Element ability : abilityElements) {
                    String abilityText = ability.text();
                    abilities.add(abilityText);
                    
                    // Parse ability into targeting chains
                    TargetingChain chain = llm.parseText(abilityText);
                    
                    // Update pattern frequencies
                    String pattern = chain.toString();
                    abilityPatterns.computeIfAbsent(gameName, k -> new HashMap<>())
                        .merge(pattern, 1, Integer::sum);
                }
            }
            
            // Save abilities for this game
            gameAbilities.put(gameName, abilities);
            
            // Save to CSV
            saveToCSV(gameName, abilities);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveToCSV(String gameName, List<String> abilities) {
        String filename = outputDir + "/" + gameName.toLowerCase().replace(" ", "_") + "_abilities.csv";
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            // Write header
            writer.writeNext(new String[]{"Ability Text", "Targeting Pattern"});
            
            // Write data
            for (String ability : abilities) {
                TargetingChain chain = llm.parseText(ability);
                String pattern = chain.toString();
                
                writer.writeNext(new String[]{ability, pattern});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void analyzePatterns() {
        // Create a CSV file for pattern analysis
        String patternFile = outputDir + "/pattern_analysis.csv";
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(patternFile))) {
            // Write header
            writer.writeNext(new String[]{"Game", "Pattern", "Frequency"});
            
            // Write pattern data
            for (Map.Entry<String, Map<String, Integer>> gameEntry : abilityPatterns.entrySet()) {
                String game = gameEntry.getKey();
                for (Map.Entry<String, Integer> patternEntry : gameEntry.getValue().entrySet()) {
                    writer.writeNext(new String[]{
                        game,
                        patternEntry.getKey(),
                        patternEntry.getValue().toString()
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void findCommonPatterns() {
        // Create a CSV file for common patterns
        String commonPatternsFile = outputDir + "/common_patterns.csv";
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(commonPatternsFile))) {
            // Write header
            writer.writeNext(new String[]{"Pattern", "Games", "Total Frequency"});
            
            // Find patterns that appear in multiple games
            Map<String, Set<String>> patternGames = new HashMap<>();
            Map<String, Integer> patternFrequencies = new HashMap<>();
            
            for (Map.Entry<String, Map<String, Integer>> gameEntry : abilityPatterns.entrySet()) {
                String game = gameEntry.getKey();
                for (Map.Entry<String, Integer> patternEntry : gameEntry.getValue().entrySet()) {
                    String pattern = patternEntry.getKey();
                    patternGames.computeIfAbsent(pattern, k -> new HashSet<>()).add(game);
                    patternFrequencies.merge(pattern, patternEntry.getValue(), Integer::sum);
                }
            }
            
            // Write common patterns
            for (Map.Entry<String, Set<String>> entry : patternGames.entrySet()) {
                if (entry.getValue().size() > 1) { // Pattern appears in multiple games
                    writer.writeNext(new String[]{
                        entry.getKey(),
                        String.join(";", entry.getValue()),
                        patternFrequencies.get(entry.getKey()).toString()
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadFromCSV(String filename) {
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            List<String[]> rows = reader.readAll();
            
            // Skip header
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length >= 2) {
                    String game = row[0];
                    String ability = row[1];
                    
                    gameAbilities.computeIfAbsent(game, k -> new ArrayList<>()).add(ability);
                    
                    // Parse ability into targeting chains
                    TargetingChain chain = llm.parseText(ability);
                    String pattern = chain.toString();
                    abilityPatterns.computeIfAbsent(game, k -> new HashMap<>())
                        .merge(pattern, 1, Integer::sum);
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
    
    public Map<String, List<String>> getGameAbilities() {
        return gameAbilities;
    }
    
    public Map<String, Map<String, Integer>> getAbilityPatterns() {
        return abilityPatterns;
    }

    public void scrapeCardUrls(String game) {
        // TODO: Implement actual scraping
        cardUrls.add("https://example.com/card1");
        cardUrls.add("https://example.com/card2");
    }

    public List<Card> scrapeCardData() {
        for (String url : cardUrls) {
            // TODO: Implement actual scraping
            Map<String, String> cardData = new HashMap<>();
            cardData.put("name", "Example Card");
            cardData.put("type", "Creature");
            cardData.put("text", "Deal 2 damage to target creature");
            
            Card card = new Card(
                cardData.get("name"),
                cardData.get("type"),
                cardData.get("text")
            );
            
            TargetingChain chain = llm.parseText(card.getText());
            card.setTargetingChain(chain);
            
            cards.add(card);
        }
        return cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void saveToCSV(String filename) throws IOException {
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);
        
        // Write header
        writer.write("name,type,text\n");
        
        // Write data
        for (Card card : cards) {
            writer.write(String.format("%s,%s,%s\n",
                escapeCsv(card.getName()),
                escapeCsv(card.getType()),
                escapeCsv(card.getText())
            ));
        }
        
        writer.close();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
} 