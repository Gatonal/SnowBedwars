package com.roberto.snow.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

/** PlaceholderAPI é opcional; reflexão mantém o Snow inicializável sem ela. */
public final class Placeholders {
    private Placeholders() { }
    public static String apply(Player player, String text) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return text;
        try { Class<?> api=Class.forName("me.clip.placeholderapi.PlaceholderAPI"); Method method=api.getMethod("setPlaceholders",Player.class,String.class); return (String)method.invoke(null,player,text); }
        catch (Exception ignored) { return text; }
    }
}
