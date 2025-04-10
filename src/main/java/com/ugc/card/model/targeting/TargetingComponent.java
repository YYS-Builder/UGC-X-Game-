package com.ugc.card.model.targeting;

import java.util.Objects;

public class TargetingComponent {
    private final ComponentCategory category;
    private final String text;

    public TargetingComponent(ComponentCategory category, String text) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        
        this.category = category;
        this.text = text.toLowerCase().trim();
    }

    public ComponentCategory getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return text;
    }

    public String getName() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetingComponent that = (TargetingComponent) o;
        return category == that.category &&
               Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, text);
    }

    @Override
    public String toString() {
        return "TargetingComponent{" +
               "category=" + category +
               ", text='" + text + '\'' +
               '}';
    }
} 