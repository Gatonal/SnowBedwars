package com.roberto.snow;

import com.roberto.snow.arena.*;
import com.roberto.snow.command.*;
import com.roberto.snow.listener.GameListener;
import com.roberto.snow.scoreboard.ScoreboardService;
import com.roberto.snow.shop.ShopService;
import com.roberto.snow.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Snow extends JavaPlugin {
    private FileConfiguration language;
    private Messages messages;
    private ArenaManager arenaManager;
    private ScoreboardService scoreboards;
    private ShopService shopService;
    private WorldRestorer worldRestorer;
    private final Map<UUID,Arena> rejoin=new HashMap<UUID,Arena>();
    @Override public void onEnable(){
        saveDefaultConfig(); loadLanguage(); messages=new Messages(this); worldRestorer=new WorldRestorer(this); arenaManager=new ArenaManager(this); scoreboards=new ScoreboardService(this); shopService=new ShopService(this);
        getServer().getPluginManager().registerEvents(new GameListener(this),this);
        SnowCommand main=new SnowCommand(this);getCommand("bw").setExecutor(main);getCommand("bw").setTabCompleter(main);getCommand("leave").setExecutor(new QuickCommand(this,"leave"));getCommand("rejoin").setExecutor(new QuickCommand(this,"rejoin"));getCommand("shout").setExecutor(new QuickCommand(this,"shout"));
        getServer().getScheduler().runTaskTimer(this,new Runnable(){@Override public void run(){arenaManager.tick();scoreboards.updateAll();}},20L,20L);
        getLogger().info("Snow BedWars habilitado para Spigot 1.8.8/1.8.9.");
    }
    @Override public void onDisable(){if(scoreboards!=null)for(Player player:getServer().getOnlinePlayers())scoreboards.clear(player);}
    public void reloadSnow(){reloadConfig();loadLanguage();arenaManager.load();}
    private void loadLanguage(){File file=new File(getDataFolder(),"language.yml");if(!file.exists())saveResource("language.yml",false);language=YamlConfiguration.loadConfiguration(file);}
    public FileConfiguration getLanguage(){return language;} public Messages getMessages(){return messages;} public ArenaManager getArenaManager(){return arenaManager;} public ScoreboardService getScoreboards(){return scoreboards;} public ShopService getShopService(){return shopService;} public WorldRestorer getWorldRestorer(){return worldRestorer;}
    public void rememberForRejoin(Player player){Arena arena=arenaManager.getArena(player);if(arena!=null)rejoin.put(player.getUniqueId(),arena);} public Arena consumeRejoin(Player player){return rejoin.remove(player.getUniqueId());}
}
