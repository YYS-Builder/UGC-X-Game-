package com.ugc.card.model;

import com.ugc.card.model.targeting.TargetingChain;
import lombok.Data;

@Data
public class Card {
    private String name;
    private String type;
    private String text;
    private TargetingChain targetingChain;

    public Card(String name, String type, String text) {
        this.name = name;
        this.type = type;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TargetingChain getTargetingChain() {
        return targetingChain;
    }

    public void setTargetingChain(TargetingChain targetingChain) {
        this.targetingChain = targetingChain;
    }
} 