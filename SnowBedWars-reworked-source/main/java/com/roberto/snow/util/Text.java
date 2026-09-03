package com.roberto.snow.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public final class Text {
    private Text() { }
    public static String color(String value) { return ChatColor.translateAlternateColorCodes('&', value == null ? "" : value); }
    public static String message(FileConfiguration language, String path, Map<String, String> replacements) {
        String value = language.getString(path, path);
        if (replacements != null) for (Map.Entry<String, String> entry : replacements.entrySet()) value = value.replace(entry.getKey(), entry.getValue());
        return color(value);
    }
}
