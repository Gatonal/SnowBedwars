package com.roberto.snow.cosmetic;

import com.roberto.snow.Snow;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public final class CosmeticManager {
    public static final String TITLE="§dCosméticos"; private final Snow plugin; public CosmeticManager(Snow plugin){this.plugin=plugin;}
    public void open(Player p){Inventory i=plugin.getServer().createInventory(null,9,TITLE);i.setItem(3,button(Material.TNT,"§cExplosão de cama","bed-explosion"));i.setItem(4,button(Material.SKULL_ITEM,"§fSom de abate","kill-sound"));i.setItem(5,button(Material.NETHER_STAR,"§aVitória","victory"));p.openInventory(i);}
    public void select(Player p,int slot){String[] keys={"bed-explosion","kill-sound","victory"};if(slot<3||slot>5)return;String key=keys[slot-3];plugin.getConfig().set("players."+p.getUniqueId()+".cosmetics."+key,"default");plugin.saveConfig();p.sendMessage("§aCosmético equipado: §f"+key);}
    public void bedBreak(Player p){p.getWorld().playEffect(p.getLocation(),Effect.EXPLOSION_LARGE,0);p.playSound(p.getLocation(),Sound.ANVIL_USE,1F,.7F);}
    public void kill(Player p,boolean fin){p.playSound(p.getLocation(),fin?Sound.ENDERDRAGON_GROWL:Sound.ORB_PICKUP,1F,1F);}
    private ItemStack button(Material m,String title,String key){ItemStack s=new ItemStack(m);ItemMeta meta=s.getItemMeta();meta.setDisplayName(title);meta.setLore(Collections.singletonList("§7Clique para equipar"));s.setItemMeta(meta);return s;}
}
