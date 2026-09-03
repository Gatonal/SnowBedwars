package com.roberto.snow.prestige;

import com.roberto.snow.Snow;
import com.roberto.snow.api.SnowEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import com.roberto.snow.util.Text;

public final class PrestigeManager {
    private final Snow plugin; public PrestigeManager(Snow plugin){this.plugin=plugin;}
    public int getLevel(UUID id){return plugin.getConfig().getInt("players."+id+".level",1);} public int getXp(UUID id){return plugin.getConfig().getInt("players."+id+".xp",0);}
    public void addXp(Player player,int base){int xp=getXp(player.getUniqueId())+(int)(base*plugin.getConfig().getDouble("prestige.global-xp-multiplier",1D));int level=getLevel(player.getUniqueId());while(xp>=cost(level)){xp-=cost(level++);player.sendMessage("§6§lNível " + level + " alcançado!");player.playSound(player.getLocation(),Sound.LEVEL_UP,1F,1F);}String root="players."+player.getUniqueId()+".";plugin.getConfig().set(root+"xp",xp);plugin.getConfig().set(root+"level",level);plugin.saveConfig();}
    public String tag(Player player){int level=getLevel(player.getUniqueId()), floor=0;YamlConfiguration file=YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(),"prestiges.yml"));if(file.getConfigurationSection("prestiges")!=null)for(String key:file.getConfigurationSection("prestiges").getKeys(false))try{int n=Integer.parseInt(key.substring("level-".length()));if(n<=level&&n>=floor)floor=n;}catch(Exception ignored){}return Text.color(file.getString("prestiges.level-"+floor+".tag","&7[{level}✫]").replace("{level}",String.valueOf(level)))+" §r";}
    private int cost(int level){return 100+(level-1)*25;}
}
