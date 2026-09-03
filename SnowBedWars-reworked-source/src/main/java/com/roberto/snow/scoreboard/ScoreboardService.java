package com.roberto.snow.scoreboard;

import com.roberto.snow.Snow;
import com.roberto.snow.arena.*;
import com.roberto.snow.util.Text;
import com.roberto.snow.util.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import java.util.*;

/** Atualiza apenas prefixos de teams já existentes; não recria objective e evita flicker. */
public final class ScoreboardService {
    private final Snow plugin; private final Map<UUID,Board> boards=new HashMap<UUID,Board>();
    public ScoreboardService(Snow plugin){this.plugin=plugin;}
    public void update(Player player,Arena arena){ Board board=boards.get(player.getUniqueId()); if(board==null){board=new Board(player);boards.put(player.getUniqueId(),board);} board.render(arena); }
    public void clear(Player player){boards.remove(player.getUniqueId());player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());}
    public void updateAll(){for(Player player:Bukkit.getOnlinePlayers()){Arena arena=plugin.getArenaManager().getArena(player);if(arena!=null)update(player,arena);}}
    private final class Board {
        private final Player player; private final Scoreboard scoreboard; private final Objective objective; private final Team[] lines=new Team[8]; private final String[] entries={"§0§r","§1§r","§2§r","§3§r","§4§r","§5§r","§6§r","§7§r"}; private final String[] previous=new String[8];
        Board(Player player){this.player=player;scoreboard=Bukkit.getScoreboardManager().getNewScoreboard();objective=scoreboard.registerNewObjective("snow","dummy");objective.setDisplaySlot(DisplaySlot.SIDEBAR);objective.setDisplayName(Text.color(plugin.getConfig().getString("scoreboard.title","&1[&bSnow&1]")));for(int i=0;i<lines.length;i++){lines[i]=scoreboard.registerNewTeam("snow"+i);lines[i].addEntry(entries[i]);objective.getScore(entries[i]).setScore(lines.length-i);}player.setScoreboard(scoreboard);}
        void render(Arena arena){Arena.PlayerStats stats=arena.getStats(player.getUniqueId()); List<String> text=new ArrayList<String>();text.add("&7"+time(arena.getGameSeconds()));text.add("&fEstado: &b"+arena.getState().name());for(BedWarsTeam team:arena.getTeams())text.add(team.getColorCode()+team.getName().charAt(0)+" &f"+(team.isBedAlive()?"✔":"✘"));text.add("&fKills: &b"+stats.getKills());text.add("&fFinals: &b"+stats.getFinalKills());text.add("&fCamas: &b"+stats.getBeds());for(int i=0;i<lines.length;i++)set(i,i<text.size()?Text.color(text.get(i)):" ");}
        void set(int index,String value){value=Placeholders.apply(player,value);if(value.equals(previous[index]))return;previous[index]=value; lines[index].setPrefix(value.length()>16?value.substring(0,16):value);}
        String time(int seconds){return String.format("%02d:%02d",seconds/60,seconds%60);}
    }
}
