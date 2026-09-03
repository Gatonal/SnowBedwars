package com.roberto.snow.quest;

import com.roberto.snow.Snow;
import com.roberto.snow.prestige.PrestigeManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

/** File-backed daily/weekly progress. Configured targets are intentionally deterministic and reset by epoch. */
public final class QuestManager {
    public static final String TITLE="§9Desafios";
    private final Snow plugin;
    public QuestManager(Snow plugin){this.plugin=plugin;}
    public void progress(Player player, QuestType type, int amount){
        long now=System.currentTimeMillis(); String root="players."+player.getUniqueId()+".quests.";
        for(String period:new String[]{"daily","weekly"}){
            long interval=period.equals("daily")?86400000L:604800000L; String path=root+period;
            if(plugin.getConfig().getLong(path+".reset",0L)<=now){plugin.getConfig().set(path+".reset",now+interval);plugin.getConfig().set(path+".progress",null);plugin.getConfig().set(path+".claimed",null);}
            String key=type.name().toLowerCase(); int target=period.equals("daily")?plugin.getConfig().getInt("quests.daily-target",5):plugin.getConfig().getInt("quests.weekly-target",25);
            int value=Math.min(target,plugin.getConfig().getInt(path+".progress."+key,0)+amount);plugin.getConfig().set(path+".progress."+key,value);
        } plugin.saveConfig();
    }
    public void open(Player player){Inventory inv=plugin.getServer().createInventory(null,27,TITLE); int slot=10; for(QuestType type:QuestType.values()){int daily=read(player,"daily",type), weekly=read(player,"weekly",type); inv.setItem(slot++,item(type,daily,weekly));} player.openInventory(inv);}
    public void claim(Player player, int rawSlot){if(rawSlot<10||rawSlot>=15)return; QuestType type=QuestType.values()[rawSlot-10]; String root="players."+player.getUniqueId()+".quests."; boolean rewarded=false;
        for(String period:new String[]{"daily","weekly"}){String path=root+period;int target=period.equals("daily")?plugin.getConfig().getInt("quests.daily-target",5):plugin.getConfig().getInt("quests.weekly-target",25);String k=type.name().toLowerCase(); if(!plugin.getConfig().getBoolean(path+".claimed."+k)&&plugin.getConfig().getInt(path+".progress."+k)>=target){plugin.getConfig().set(path+".claimed."+k,true); plugin.getPrestiges().addXp(player,period.equals("daily")?50:250);rewarded=true;}}
        if(rewarded){plugin.saveConfig();player.sendMessage("§aRecompensa de desafio resgatada!");open(player);} }
    private int read(Player p,String period,QuestType type){return plugin.getConfig().getInt("players."+p.getUniqueId()+".quests."+period+".progress."+type.name().toLowerCase(),0);}
    private ItemStack item(QuestType type,int daily,int weekly){ItemStack stack=new ItemStack(Material.PAPER);ItemMeta meta=stack.getItemMeta();meta.setDisplayName("§e"+type.name());meta.setLore(Arrays.asList("§7Diário: §f"+daily+"/"+plugin.getConfig().getInt("quests.daily-target",5),"§7Semanal: §f"+weekly+"/"+plugin.getConfig().getInt("quests.weekly-target",25),"§aClique para resgatar quando concluir"));stack.setItemMeta(meta);return stack;}
}
