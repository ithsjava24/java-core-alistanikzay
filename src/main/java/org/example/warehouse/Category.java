package org.example.warehouse;

import java.util.HashSet;

public class Category {
    private final String name;
    private static final HashSet<Category> categoriesSet = new HashSet<>(); // Använder HashSet för att undvika duplicering

    private Category(String name) {
        this.name = name;
        categoriesSet.add(this); // Lägger till kategorin i mängden
    }

    public String getName() {
        return name;
    }

    public static Category of(String name) {
        validateName(name); // Validera kategorinamnet
        String formattedName = capitalizeName(name); // Kapitalisera namnet

        return categoriesSet.stream()
                .filter(category -> category.getName().equals(formattedName)) // Sök efter befintlig kategori
                .findFirst()
                .orElseGet(() -> new Category(formattedName)); // Skapa ny kategori om den inte finns
    }

    private static void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Category name can't be null");
        }
    }

    private static String capitalizeName(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1); // Kapitalisera första bokstaven
    }
}
