package com.roberto.snow.arena;

import com.roberto.snow.Snow;
import com.roberto.snow.generator.ResourceGenerator;
import com.roberto.snow.api.BedWarsGameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public final class Arena {
    private final Snow plugin;
    private final String id;
    private final ArenaMode mode;
    private final Map<String, BedWarsTeam> teams = new LinkedHashMap<String, BedWarsTeam>();
    private final Map<UUID, PlayerStats> stats = new HashMap<UUID, PlayerStats>();
    private final Set<UUID> players = new LinkedHashSet<UUID>();
    private final List<ResourceGenerator> generators = new ArrayList<ResourceGenerator>();
    private ArenaState state = ArenaState.WAITING;
    private int stateSeconds, gameSeconds;
    public Arena(Snow plugin, String id, ArenaMode mode) { this.plugin=plugin; this.id=id; this.mode=mode; createTeams(); }
    private void createTeams() {
        String[][] values = {{"red", "Vermelho", "&c"}, {"blue", "Azul", "&9"}, {"green", "Verde", "&a"}, {"yellow", "Amarelo", "&e"}};
        org.bukkit.DyeColor[] colors = {org.bukkit.DyeColor.RED, org.bukkit.DyeColor.BLUE, org.bukkit.DyeColor.GREEN, org.bukkit.DyeColor.YELLOW};
        for (int i=0;i<mode.getTeams();i++) teams.put(values[i][0], new BedWarsTeam(values[i][0], values[i][1], values[i][2], colors[i]));
    }
    public boolean join(Player player) {
        if (state != ArenaState.WAITING || players.size() >= mode.getMaxPlayers()) return false;
        BedWarsTeam target = smallestTeam(); if (target == null || !target.add(player, mode.getPlayersPerTeam())) return false;
        players.add(player.getUniqueId()); stats.put(player.getUniqueId(), new PlayerStats());
        if (players.size() >= plugin.getConfig().getInt("arena.minimum-players", 2)) beginStarting();
        return true;
    }
    public void leave(Player player, boolean reconnectable) {
        if (reconnectable) return;
        UUID uuid = player.getUniqueId(); players.remove(uuid);
        BedWarsTeam team = getTeam(uuid); if (team != null) team.remove(uuid);
        if (!reconnectable) stats.remove(uuid);
        if (state == ArenaState.STARTING && players.size() < plugin.getConfig().getInt("arena.minimum-players", 2)) { state=ArenaState.WAITING; stateSeconds=0; }
        if (state == ArenaState.PLAYING && livingTeams() <= 1) finish();
    }
    public void tickSecond() {
        if (state == ArenaState.STARTING && --stateSeconds <= 0) start();
        else if (state == ArenaState.ENDING && --stateSeconds <= 0) restart();
        else if (state == ArenaState.PLAYING) { gameSeconds++; for (ResourceGenerator generator : generators) generator.tick(); }
    }
    private void beginStarting() { state=ArenaState.STARTING; stateSeconds=plugin.getConfig().getInt("arena.countdown-seconds", 30); }
    private void start() {
        state=ArenaState.PLAYING; gameSeconds=0;
        Bukkit.getPluginManager().callEvent(new BedWarsGameStartEvent(this));
        for (UUID uuid : players) { Player player=Bukkit.getPlayer(uuid); if (player != null) { BedWarsTeam team=getTeam(uuid); if (team != null && team.getSpawn()!=null) player.teleport(team.getSpawn()); preparePlayer(player); plugin.getMessages().send(player, "game-started"); } }
    }
    public void finish() { if (state == ArenaState.ENDING || state == ArenaState.RESTARTING) return; state=ArenaState.ENDING; stateSeconds=plugin.getConfig().getInt("arena.ending-seconds", 10); for(UUID uuid:players){Player p=Bukkit.getPlayer(uuid);if(p!=null)plugin.getQuests().progress(p, com.roberto.snow.quest.QuestType.WIN, 1);} }
    private void restart() { state=ArenaState.RESTARTING; plugin.getWorldRestorer().restore(this); }
    public void resetComplete() { state=ArenaState.WAITING; stateSeconds=0; gameSeconds=0; players.clear(); stats.clear(); for (BedWarsTeam team:teams.values()) { team.restoreBed(); team.resetUpgrades(); for (UUID uuid:new ArrayList<UUID>(team.getMembers())) team.remove(uuid); } }
    public void preparePlayer(Player player) { player.setGameMode(org.bukkit.GameMode.SURVIVAL); player.setHealth(player.getMaxHealth()); player.setFoodLevel(20); player.setFireTicks(0); player.getInventory().clear(); player.getInventory().setArmorContents(null); player.getInventory().setItem(0,new org.bukkit.inventory.ItemStack(org.bukkit.Material.WOOD_SWORD)); }
    public boolean isPlayingMember(UUID uuid) { return state==ArenaState.PLAYING && players.contains(uuid) && getTeam(uuid)!=null; }
    private BedWarsTeam smallestTeam() { BedWarsTeam target=null; for (BedWarsTeam team:teams.values()) if (team.getMembers().size()<mode.getPlayersPerTeam() && (target==null || team.getMembers().size()<target.getMembers().size())) target=team; return target; }
    private int livingTeams() { int count=0; for (BedWarsTeam t:teams.values()) if (t.isBedAlive() || !t.getMembers().isEmpty()) count++; return count; }
    public String getId() { return id; } public ArenaMode getMode() { return mode; } public ArenaState getState() { return state; } public int getStateSeconds() { return stateSeconds; } public int getGameSeconds() { return gameSeconds; }
    public Collection<BedWarsTeam> getTeams() { return Collections.unmodifiableCollection(teams.values()); }
    public Set<UUID> getPlayers() { return Collections.unmodifiableSet(players); }
    public BedWarsTeam getTeam(UUID uuid) { for (BedWarsTeam team:teams.values()) if (team.contains(uuid)) return team; return null; }
    public BedWarsTeam getTeam(String id) { return teams.get(id.toLowerCase()); }
    public PlayerStats getStats(UUID uuid) { PlayerStats current=stats.get(uuid); if(current==null) {current=new PlayerStats();stats.put(uuid,current);} return current; }
    public List<ResourceGenerator> getGenerators() { return generators; }
    public void addGenerator(ResourceGenerator generator) { generators.add(generator); }
    public static final class PlayerStats { private int kills, finalKills, beds; public int getKills(){return kills;} public int getFinalKills(){return finalKills;} public int getBeds(){return beds;} public void kill(boolean fin){kills++;if(fin)finalKills++;} public void bed(){beds++;} }
}
