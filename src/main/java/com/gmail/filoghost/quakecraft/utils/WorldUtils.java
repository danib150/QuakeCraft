package com.gmail.filoghost.quakecraft.utils;

public class WorldUtils {
    public static String capitalizeFully(String text) {
        if (text == null || text.isBlank()) return text;

        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return sb.toString().trim();
    }

    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
