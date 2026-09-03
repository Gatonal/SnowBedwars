package com.roberto.snow.hotbar;

import com.roberto.snow.Snow;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public final class HotbarManager {
    private final Snow plugin; public HotbarManager(Snow plugin){this.plugin=plugin;}
    public void giveLobbyItems(Player p){give(p,0,Material.COMPASS,"§aSeletor de arena");give(p,4,Material.NETHER_STAR,"§dCosméticos");give(p,8,Material.REDSTONE_COMPARATOR,"§eDesafios");}
    private void give(Player p,int slot,Material m,String name){ItemStack s=new ItemStack(m);ItemMeta meta=s.getItemMeta();meta.setDisplayName(name);s.setItemMeta(meta);p.getInventory().setItem(slot,s);}
}
