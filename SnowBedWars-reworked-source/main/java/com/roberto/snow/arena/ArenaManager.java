package com.roberto.snow.arena;

import com.roberto.snow.Snow;
import com.roberto.snow.generator.GeneratorType;
import com.roberto.snow.generator.ResourceGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public final class ArenaManager {
    private final Snow plugin; private final Map<String,Arena> arenas=new LinkedHashMap<String,Arena>(); private final Map<UUID,Arena> byPlayer=new HashMap<UUID,Arena>();
    public ArenaManager(Snow plugin) { this.plugin=plugin; load(); }
    public void load() { arenas.clear(); ConfigurationSection root=plugin.getConfig().getConfigurationSection("arenas"); if(root==null)return; for(String id:root.getKeys(false)){ ArenaMode mode=ArenaMode.parse(root.getString(id+".mode","solo")); if(mode==null)continue; Arena arena=new Arena(plugin,id,mode); for(BedWarsTeam team:arena.getTeams()){ team.setSpawn((Location)plugin.getConfig().get(idPath(id,"teams."+team.getId()+".spawn"))); team.setBed((Location)plugin.getConfig().get(idPath(id,"teams."+team.getId()+".bed"))); } ConfigurationSection gens=plugin.getConfig().getConfigurationSection(idPath(id,"generators")); if(gens!=null) for(String key:gens.getKeys(false)){ Location location=(Location)gens.get(key+".location"); GeneratorType type=GeneratorType.parse(gens.getString(key+".type")); if(location!=null&&type!=null) arena.addGenerator(new ResourceGenerator(plugin,type,location)); } arenas.put(id.toLowerCase(),arena); } }
    public Arena create(String id,ArenaMode mode) { Arena arena=new Arena(plugin,id,mode); arenas.put(id.toLowerCase(),arena); plugin.getConfig().set(idPath(id,"mode"),mode.name().toLowerCase()); plugin.saveConfig(); return arena; }
    public boolean join(Player player,String id) { if(byPlayer.containsKey(player.getUniqueId()))return false; Arena arena=arenas.get(id.toLowerCase()); if(arena==null||!arena.join(player))return false; byPlayer.put(player.getUniqueId(),arena); return true; }
    public void leave(Player player,boolean reconnectable) { Arena arena=byPlayer.remove(player.getUniqueId()); if(arena!=null)arena.leave(player,reconnectable); }
    public Arena getArena(Player player){return byPlayer.get(player.getUniqueId());} public Arena getArena(String id){return arenas.get(id.toLowerCase());} public Collection<Arena> getArenas(){return Collections.unmodifiableCollection(arenas.values());}
    public void tick(){for(Arena a:arenas.values())a.tickSecond();}
    public void completeReset(Arena arena){Iterator<Map.Entry<UUID,Arena>> iterator=byPlayer.entrySet().iterator();while(iterator.hasNext())if(iterator.next().getValue()==arena)iterator.remove();arena.resetComplete();}
    public void setSpawn(Arena arena,String team,Location location){ BedWarsTeam t=arena.getTeam(team); if(t==null)return;t.setSpawn(location); plugin.getConfig().set(idPath(arena.getId(),"teams."+t.getId()+".spawn"),location);plugin.saveConfig(); }
    public void setBed(Arena arena,String team,Location location){ BedWarsTeam t=arena.getTeam(team); if(t==null)return;t.setBed(location); plugin.getConfig().set(idPath(arena.getId(),"teams."+t.getId()+".bed"),location);plugin.saveConfig(); }
    public void addGenerator(Arena arena,GeneratorType type,Location location){ arena.addGenerator(new ResourceGenerator(plugin,type,location)); String key=String.valueOf(arena.getGenerators().size()); plugin.getConfig().set(idPath(arena.getId(),"generators."+key+".type"),type.name());plugin.getConfig().set(idPath(arena.getId(),"generators."+key+".location"),location);plugin.saveConfig(); }
    private String idPath(String id,String path){return "arenas."+id+"."+path;}
}
