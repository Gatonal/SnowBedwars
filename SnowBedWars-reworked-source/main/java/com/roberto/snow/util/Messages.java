package com.roberto.snow.util;

import com.roberto.snow.Snow;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.Map;

public final class Messages {
    private final Snow plugin;
    public Messages(Snow plugin) { this.plugin = plugin; }
    public String get(String key) { return get(key, Collections.<String, String>emptyMap()); }
    public String get(String key, Map<String, String> variables) { return Text.message(plugin.getLanguage(), key, variables); }
    public void send(CommandSender sender, String key) { sender.sendMessage(get("prefix") + get(key)); }
    public void send(CommandSender sender, String key, Map<String, String> variables) { sender.sendMessage(get("prefix") + get(key, variables)); }
}
