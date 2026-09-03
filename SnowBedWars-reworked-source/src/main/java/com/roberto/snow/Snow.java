package com.roberto.snow;

import com.roberto.snow.arena.*;
import com.roberto.snow.command.*;
import com.roberto.snow.listener.GameListener;
import com.roberto.snow.scoreboard.ScoreboardService;
import com.roberto.snow.shop.ShopService;
import com.roberto.snow.util.Messages;
import com.roberto.snow.quest.QuestManager;
import com.roberto.snow.prestige.PrestigeManager;
import com.roberto.snow.cosmetic.CosmeticManager;
import com.roberto.snow.hotbar.HotbarManager;
import com.roberto.snow.combat.CombatListener;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.roberto.snow.quest.QuestType;
import com.roberto.snow.api.BedWarsKillEvent;

public final class Snow extends JavaPlugin {
    private FileConfiguration language;
    private Messages messages;
    private ArenaManager arenaManager;
    private ScoreboardService scoreboards;
    private ShopService shopService;
    private WorldRestorer worldRestorer;
    private QuestManager quests; private PrestigeManager prestiges; private CosmeticManager cosmetics; private HotbarManager hotbar;
    private final Map<UUID,Arena> rejoin=new HashMap<UUID,Arena>();
    @Override public void onEnable(){
        saveDefaultConfig(); if(!new File(getDataFolder(),"prestiges.yml").exists())saveResource("prestiges.yml",false); loadLanguage(); messages=new Messages(this); worldRestorer=new WorldRestorer(this); arenaManager=new ArenaManager(this); scoreboards=new ScoreboardService(this); shopService=new ShopService(this); quests=new QuestManager(this); prestiges=new PrestigeManager(this); cosmetics=new CosmeticManager(this); hotbar=new HotbarManager(this);
        getServer().getPluginManager().registerEvents(new GameListener(this),this);
        getServer().getPluginManager().registerEvents(new CombatListener(this),this);
        SnowCommand main=new SnowCommand(this);getCommand("bw").setExecutor(main);getCommand("bw").setTabCompleter(main);getCommand("leave").setExecutor(new QuickCommand(this,"leave"));getCommand("rejoin").setExecutor(new QuickCommand(this,"rejoin"));getCommand("shout").setExecutor(new QuickCommand(this,"shout"));
        getServer().getScheduler().runTaskTimer(this,new Runnable(){@Override public void run(){arenaManager.tick();scoreboards.updateAll();}},20L,20L);
        getLogger().info("Snow BedWars habilitado para Spigot 1.8.8/1.8.9.");
    }
    @Override public void onDisable(){if(scoreboards!=null)for(Player player:getServer().getOnlinePlayers())scoreboards.clear(player);}
    public void reloadSnow(){reloadConfig();loadLanguage();arenaManager.load();}
    private void loadLanguage(){File file=new File(getDataFolder(),"language.yml");if(!file.exists())saveResource("language.yml",false);language=YamlConfiguration.loadConfiguration(file);}
    public FileConfiguration getLanguage(){return language;} public Messages getMessages(){return messages;} public ArenaManager getArenaManager(){return arenaManager;} public ScoreboardService getScoreboards(){return scoreboards;} public ShopService getShopService(){return shopService;} public WorldRestorer getWorldRestorer(){return worldRestorer;} public QuestManager getQuests(){return quests;} public PrestigeManager getPrestiges(){return prestiges;} public CosmeticManager getCosmetics(){return cosmetics;} public HotbarManager getHotbar(){return hotbar;}
    public void rememberForRejoin(Player player){Arena arena=arenaManager.getArena(player);if(arena!=null)rejoin.put(player.getUniqueId(),arena);} public Arena consumeRejoin(Player player){return rejoin.remove(player.getUniqueId());}
    /** Central lethal flow: damage is cancelled, avoiding the vanilla death screen. */
    public void defeat(final Player victim, Player killer) {
        final Arena arena=arenaManager.getArena(victim); if(arena==null || !arena.isPlayingMember(victim.getUniqueId())) return;
        BedWarsTeam victimTeam=arena.getTeam(victim.getUniqueId()); boolean finalKill=victimTeam==null || !victimTeam.isBedAlive();
        if(killer!=null && killer!=victim && arena.getTeam(killer.getUniqueId())!=victimTeam) {
            arena.getStats(killer.getUniqueId()).kill(finalKill); quests.progress(killer,QuestType.KILL,1); if(finalKill) quests.progress(killer,QuestType.FINAL_KILL,1);
            prestiges.addXp(killer,finalKill?25:10); cosmetics.kill(killer,finalKill); getServer().getPluginManager().callEvent(new BedWarsKillEvent(arena,killer,victim,finalKill));
        }
        victim.getInventory().clear(); victim.setFireTicks(0);
        if(finalKill) { victim.setGameMode(org.bukkit.GameMode.SPECTATOR); victim.sendMessage("§c§lELIMINADO!"); arena.leave(victim,false); return; }
        victim.setGameMode(org.bukkit.GameMode.SPECTATOR); victim.sendMessage("§cVocê morreu. Renascendo...");
        getServer().getScheduler().runTaskLater(this,new Runnable(){public void run(){ if(arena.getState()==ArenaState.PLAYING && arena.getTeam(victim.getUniqueId())!=null) { BedWarsTeam team=arena.getTeam(victim.getUniqueId()); if(team.getSpawn()!=null) victim.teleport(team.getSpawn()); arena.preparePlayer(victim); } }}, 40L);
    }
}
