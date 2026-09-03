package com.roberto.snow.command;
import com.roberto.snow.Snow;
import com.roberto.snow.arena.Arena;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
public final class QuickCommand implements CommandExecutor {
    private final Snow plugin; private final String action; public QuickCommand(Snow plugin,String action){this.plugin=plugin;this.action=action;}
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args){if(!(sender instanceof Player)){plugin.getMessages().send(sender,"player-only");return true;}Player player=(Player)sender;if("leave".equals(action)){plugin.getArenaManager().leave(player,false);plugin.getScoreboards().clear(player);return true;}if("rejoin".equals(action)){Arena arena=plugin.consumeRejoin(player);if(arena==null||!plugin.getArenaManager().rejoin(player,arena))sender.sendMessage("§1[§bSnow§1] §cNão há uma partida disponível para reconectar.");else sender.sendMessage("§aVocê retornou à partida.");return true;}if("shout".equals(action)){Arena arena=plugin.getArenaManager().getArena(player);if(arena!=null&&args.length>0)for(java.util.UUID id:arena.getPlayers()){Player other=org.bukkit.Bukkit.getPlayer(id);if(other!=null)other.sendMessage("§1[§bSnow§1] §b[GRITO] §f"+player.getName()+": "+String.join(" ",args));}return true;}return true;}
}
